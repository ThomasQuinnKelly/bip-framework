package gov.va.bip.framework.rest.autoconfigure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import gov.va.bip.framework.client.rest.template.RestClientTemplate;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.rest.exception.BipRestGlobalExceptionHandler;
import gov.va.bip.framework.rest.provider.aspect.ProviderHttpAspect;
import gov.va.bip.framework.rest.provider.aspect.RestProviderTimerAspect;
import gov.va.bip.framework.validation.Defense;

/**
 * A collection of spring beans used for REST server and/or client operations.
 *
 * Created by rthota on 8/24/17.
 *
 * @author akulkarni
 */
@Configuration
public class BipRestAutoConfiguration {

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRestAutoConfiguration.class);

	@Value("${bip.framework.client.rest.connectionTimeout:20000}")
	private String connectionTimeout;

	@Value("${bip.framework.client.rest.readTimeout:30000}")
	private String readTimeout;

	@Value("${bip.framework.client.rest.maxTotalPool:10}")
	private String maxTotalPool;

	@Value("${bip.framework.client.rest.defaultMaxPerRoutePool:5}")
	private String defaultMaxPerRoutePool;

	@Value("${bip.framework.client.rest.validateAfterInactivityPool:10000}")
	private String validateAfterInactivityPool;

	@Value("${bip.framework.client.rest.connectionBufferSize:4128}")
	private String connectionBufferSize;

	/**
	 * Aspect bean of the {@link ProviderHttpAspect}
	 * (currently executed before, after returning, and after throwing REST controllers).
	 *
	 * @return ProviderHttpAspect
	 */
	@Bean
	@ConditionalOnMissingBean
	public ProviderHttpAspect providerHttpAspect() {
		return new ProviderHttpAspect();
	}

	/**
	 * Bip rest global exception handler.
	 *
	 * @return the bip rest global exception handler
	 */
	@Bean
	@ConditionalOnMissingBean
	public BipRestGlobalExceptionHandler bipRestGlobalExceptionHandler() {
		return new BipRestGlobalExceptionHandler();
	}

	/**
	 * Aspect bean of the {@link RestProviderTimerAspect}
	 * (currently executed around REST controllers).
	 *
	 * @return RestProviderTimerAspect
	 */
	@Bean
	@ConditionalOnMissingBean
	public RestProviderTimerAspect restProviderTimerAspect() {
		return new RestProviderTimerAspect();
	}

	/**
	 * Http components client http request factory.
	 *
	 * @return the http components client http request factory
	 */
	@Bean
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
		int connTimeoutValue = 0;
		try {
			connTimeoutValue = Integer.valueOf(connectionTimeout);
		} catch (NumberFormatException e) { // NOSONAR intentionally do nothing
			// let the Defense below take care of it
			LOGGER.warn("NumberFormatException occurred");
		}
		Defense.state(connTimeoutValue > 0,
				"Invalid settings: Connection Timeout value must be greater than zero.\n"
						+ "  - Ensure spring scan directive includes gov.va.bip.framework.client.rest.template;\n"
						+ "  - Application property must be set to non-zero positive integer value: bip.framework.client.rest.connection-timeout {} "
						+ connectionTimeout + ".");

		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setBufferSize(Integer.valueOf(connectionBufferSize))
				.build();
		HttpClientBuilder clientBuilder = HttpClients.custom();
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(); // NOSONAR
																												 // CloseableHttpClient#close
																												 // should
																												 // automatically
																												 // shut down the
																												 // connection pool
																												 // only if exclusively
																												 // owned by the client
		poolingConnectionManager.setMaxTotal(Integer.valueOf(maxTotalPool));
		poolingConnectionManager.setDefaultMaxPerRoute(Integer.valueOf(defaultMaxPerRoutePool));
		poolingConnectionManager.setValidateAfterInactivity(Integer.valueOf(validateAfterInactivityPool));

		clientBuilder.setConnectionManager(poolingConnectionManager);
		clientBuilder.setDefaultConnectionConfig(connectionConfig);
		setRetryHandlerToClientBuilder(clientBuilder);

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
				new HttpComponentsClientHttpRequestFactory(clientBuilder.build());
		clientHttpRequestFactory.setConnectTimeout(connTimeoutValue);
		clientHttpRequestFactory.setReadTimeout(Integer.valueOf(readTimeout));

		return clientHttpRequestFactory;
	}

	/**
	 * Sets the retry handler to client builder.
	 *
	 * @param clientBuilder the new retry handler to client builder
	 */
	private void setRetryHandlerToClientBuilder(HttpClientBuilder clientBuilder) {
		clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true, new ArrayList<>()) {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				LOGGER.info("Retry request, execution count: {}, exception: {}", executionCount, exception);
				if (exception instanceof org.apache.http.NoHttpResponseException) {
					LOGGER.warn("No response from server on {}", executionCount, " call");
					return true;
				}
				return super.retryRequest(exception, executionCount, context);
			}

		});
	}

	/**
	 * A bean that acts as a {@link RestTemplate} wrapper for executing client REST calls.
	 * <p>
	 * Useful for making non-Feign REST calls (e.g. to external partners, or public URLs)
	 * that are made in partner or library projects.
	 * <p>
	 * Capabilities / Limitations of the returned RestClientTemplate:
	 * <ul>
	 * <li><b>does</b> derive request timeout values from the application properties.
	 * <li>is <b>not</b> load balanced by the spring-cloud LoadBalancerClient.
	 * <li>does <b>not</b> attach the JWT from the current session to the outgoing request.
	 * </ul>
	 *
	 * @return RestClientTemplate
	 */
	@Bean
	@ConditionalOnMissingBean
	public RestClientTemplate restClientTemplate() {
		RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory());
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory()));

		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (StringHttpMessageConverter.class.isAssignableFrom(converter.getClass())) {
				LOGGER.debug("Casting converter to StringHttpMessageConverter");
				((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
				((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
			}
		}

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(tokenClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		return new RestClientTemplate(restTemplate);
	}

	/**
	 * A bean for internal purposes, the standard (non-feign) REST request intercepter
	 *
	 * @return TokenClientHttpRequestInterceptor
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenClientHttpRequestInterceptor tokenClientHttpRequestInterceptor() {
		return new TokenClientHttpRequestInterceptor();
	}

}
