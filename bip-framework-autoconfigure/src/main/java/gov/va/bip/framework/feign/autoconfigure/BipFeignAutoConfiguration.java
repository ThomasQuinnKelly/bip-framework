package gov.va.bip.framework.feign.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.ConfigurableEnvironment;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;

/**
 * Auto configuration for feign enabled REST clients (e.g. {@code EnableFeignClients}).
 *
 * Created by rthota on 9/05/17.
 */
@Configuration
public class BipFeignAutoConfiguration {

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipFeignAutoConfiguration.class);

	@Value("${bip.framework.rest.client.connectionTimeout:20000}")
	private String connectionTimeout;

	private String groupKey = "defaultGroup";

	/**
	 * Get the Feign group key. Default is "defaultGroup".
	 *
	 * @return String the group key
	 */
	public String getGroupKey() {
		return groupKey;
	}

	/**
	 * Get the Feign group key. Default is "defaultGroup".
	 *
	 * @param groupKey the group key
	 */
	public void setGroupKey(final String groupKey) {
		this.groupKey = groupKey;
	}
	
	/**
	 * Custom Feign Error Decoder
	 * @return
	 */
	@Bean
	public FeignCustomErrorDecoder feignCustomErrorDecoder() {
	  return new FeignCustomErrorDecoder();
	}

	/**
	 * A bean that eliminates the need to use the {@code @FeignClient(name="groupKey", url="commandKey")}
	 * annotation on Feign resource/provider classes and interfaces. It configures the returned Feign.Builder
	 * with a SetterFactory pre-configured with groupKey and commandKey.
	 * <p>
	 * Useful for making Feign enabled REST calls (e.g. {@code @EnableFeignClients})
	 * that are made from OpenShift services.
	 * <p>
	 * The bean also performs standardized configuration that can be used to execute REST calls:
	 * <ul>
	 * <li>derives request timeout values from the application properties
	 * <li>attaches the JWT from the current session to the outgoing request via {@link TokenFeignRequestInterceptor}.
	 * </ul>
	 * Additional configuration can be performed on the builder that is returned from this bean.
	 *
	 * @return Feign.Builder
	 */
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = true)
	public Feign.Builder feignBuilder() {
		int connTimeoutValue = 0;
		try {
			connTimeoutValue = Integer.valueOf(connectionTimeout);
		} catch (NumberFormatException e) { // NOSONAR intentionally do nothing
			// let the Defense below take care of it
		}
		Defense.state(connTimeoutValue > 0,
				"Invalid settings: Connection Timeout value must be greater than zero.\n"
						+ "  - Ensure spring scan directive includes gov.va.bip.framework.rest.client.resttemplate;\n"
						+ "  - Application property must be set to non-zero positive integer values for bip.framework.rest.client.connectionTimeout {} "
						+ connectionTimeout + ".");
		final int connTimeoutValueFinal = connTimeoutValue;

		/*
		 * Used by the HystrixFeign setter factory.
		 * Equivalent to:
		 * NOSONAR commandKeyIsRequestLine = SetterFactory.create(Target<?> target, Method method) {..}
		 */
		final SetterFactory commandKeyIsRequestLine = (target, method) -> {
			final String commandKey = Feign.configKey(target.type(), method);
			LOGGER.debug("Feign Hystrix Group Key: {}", groupKey);
			LOGGER.debug("Feign Hystrix Command Key: {}", commandKey);
			return HystrixCommand.Setter
					.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
					.andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
							.withExecutionTimeoutInMilliseconds(connTimeoutValueFinal));
		};

		return HystrixFeign.builder().setterFactory(commandKeyIsRequestLine).requestInterceptor(tokenFeignRequestInterceptor());
	}
	
	/**
	 * Request options.
	 *
	 * @param env the env
	 * @return the request. options
	 */
	@Bean
    Request.Options requestOptions(ConfigurableEnvironment env){
        int ribbonReadTimeout = env.getProperty("ribbon.ReadTimeout", int.class, 6000);
        int ribbonConnectionTimeout = env.getProperty("ribbon.ConnectTimeout", int.class, 3000);

        return new Request.Options(ribbonConnectionTimeout, ribbonReadTimeout);
    }
	
	/**
	 * Feign logger level.
	 *
	 * @return the logger. level
	 */
	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
    }

	/**
	 * A bean for internal purposes, the standard (non-feign) REST request intercepter
	 *
	 * @return TokenFeignRequestInterceptor
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenFeignRequestInterceptor tokenFeignRequestInterceptor() {
		return new TokenFeignRequestInterceptor();
	}

}
