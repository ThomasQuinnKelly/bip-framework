package gov.va.bip.framework.client.ws;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.wss4j.dom.WSConstants;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.client.ws.interceptor.AuditWsInterceptor;
import gov.va.bip.framework.client.ws.interceptor.AuditWsInterceptorConfig;
import gov.va.bip.framework.exception.BipPartnerRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.log.PerformanceLogMethodInterceptor;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.security.VAServiceWss4jSecurityInterceptor;
import gov.va.bip.framework.validation.Defense;
import io.jsonwebtoken.lang.Collections;

/**
 * Base WebService Client configuration, consolidates core/common web service configuration operations used across the applications.
 *
 * @author jshrader
 */
@Configuration
public class BaseWsClientConfig {

	/** Logger for this class */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BaseWsClientConfig.class);

	/** base package for framework exceptions */
	public static final String PACKAGE_FRAMEWORK_EXCEPTION = "gov.va.bip.framework.exception";

	/**
	 * Creates the default web service template using the default audit request/response interceptors and no web service interceptors.
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createDefaultWebServiceTemplate(final String endpoint, final int readTimeout,
			final int connectionTimeout, final Marshaller marshaller, final Unmarshaller unmarshaller) {
		return this.createDefaultWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, null);
	}

	/**
	 * Creates the default web service template using the default audit request/response interceptors and the provided web service
	 * interceptors
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param wsInterceptors the ws interceptors
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createDefaultWebServiceTemplate(final String endpoint, final int readTimeout,
			final int connectionTimeout, final Marshaller marshaller, final Unmarshaller unmarshaller,
			final ClientInterceptor[] wsInterceptors) {
		return this.createDefaultWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, wsInterceptors);
	}

	/**
	 * Creates the default web service template using the supplied http request/response interceptors and the provided web service
	 * interceptors with axiom message factory
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param httpRequestInterceptors the http request interceptors
	 * @param httpResponseInterceptors the http response interceptors
	 * @param wsInterceptors the ws interceptors
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createDefaultWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint, // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpRequestInterceptor[] httpRequestInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpResponseInterceptor[] httpResponseInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final ClientInterceptor[] wsInterceptors) { // NOSONAR do NOT encapsulate params just to reduce the number

		// create axiom message factory
		final AxiomSoapMessageFactory axiomSoapMessageFactory = new AxiomSoapMessageFactory();

		return this
				.createWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller, httpRequestInterceptors,
						httpResponseInterceptors, wsInterceptors, axiomSoapMessageFactory,
						null, null, null, null);
	}

	/**
	 * Creates the web service template using the the default audit request/response interceptors and the provided web service
	 * interceptors with saaj message factory.
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param wsInterceptors the ws interceptors
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SOAPException error creating message factory
	 */
	protected final WebServiceTemplate createSaajWebServiceTemplate(final String endpoint, final int readTimeout,
			final int connectionTimeout, final Marshaller marshaller, final Unmarshaller unmarshaller,
			final ClientInterceptor[] wsInterceptors) throws SOAPException {
		return this.createWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, wsInterceptors,
				new SaajSoapMessageFactory(MessageFactory.newInstance()),
				null, null, null, null);
	}

	/**
	 * Creates the ssl web service template using the default audit request/response interceptors and no web service interceptors.
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param keystore the path to the client ssl keystore
	 * @param keystorePass the pass-word for the client ssl keystore
	 * @param truststore the path to the client ssl truststore
	 * @param truststorePass the pass-word for the client ssl truststore
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createSslWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint, // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout,// NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller,// NOSONAR do NOT encapsulate params just to reduce the number
			final Resource keystore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String keystorePass, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource truststore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String truststorePass) { // NOSONAR do NOT encapsulate params just to reduce the number
		return this.createSslWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, null, keystore, keystorePass, truststore, truststorePass);
	}

	/**
	 * Creates the ssl web service template using the default audit request/response interceptors and the provided web service
	 * interceptors.
	 * <p>
	 * Auditing {@link AuditWsInterceptor} is added automatically.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param wsInterceptors the ws interceptors
	 * @param keystore the path to the client ssl keystore
	 * @param keystorePass the pass-word for the client ssl keystore
	 * @param truststore the path to the client ssl truststore
	 * @param truststorePass the pass-word for the client ssl truststore
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createSslWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint, // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout,// NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller,// NOSONAR do NOT encapsulate params just to reduce the number
			final ClientInterceptor[] wsInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource keystore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String keystorePass, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource truststore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String truststorePass) { // NOSONAR do NOT encapsulate params just to reduce the number
		return this.createSslWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, wsInterceptors, keystore, keystorePass, truststore, truststorePass);
	}

	/**
	 * Creates the ssl web service template using the supplied http request/response interceptors and the provided web service
	 * interceptors with axiom message factory
	 *
	 * {@link AuditWsInterceptor} to audit the request and response are added automatically to
	 * the {@code wsInterceptors} array of {@link ClientInterceptor}s.
	 * If the {@code wsInterceptors} array already has AuditWebserviceInterceptors at the beginning and the end
	 * of the array, the array will be left untouched. Any other instances (e.g. in the middle of the array)
	 * will be removed.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param httpRequestInterceptors the http request interceptors
	 * @param httpResponseInterceptors the http response interceptors
	 * @param wsInterceptors the ws interceptors
	 * @param keystore the path to the client ssl keystore
	 * @param keystorePass the pass-word for the client ssl keystore
	 * @param truststore the path to the client ssl truststore
	 * @param truststorePass the pass-word for the client ssl truststore
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createSslWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint, // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpRequestInterceptor[] httpRequestInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpResponseInterceptor[] httpResponseInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final ClientInterceptor[] wsInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource keystore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String keystorePass, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource truststore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String truststorePass) { // NOSONAR do NOT encapsulate params just to reduce the number

		// create axiom message factory
		final AxiomSoapMessageFactory axiomSoapMessageFactory = new AxiomSoapMessageFactory();

		return this
				.createWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller, httpRequestInterceptors,
						httpResponseInterceptors, wsInterceptors, axiomSoapMessageFactory, keystore, keystorePass, truststore,
						truststorePass);
	}

	/**
	 * Creates the SAAJ SSL web service template using the the default audit request/response interceptors and the provided web service
	 * interceptors with saaj message factory.
	 *
	 * {@link AuditWsInterceptor} to audit the request and response are added automatically to
	 * the {@code wsInterceptors} array of {@link ClientInterceptor}s.
	 * If the {@code wsInterceptors} array already has AuditWebserviceInterceptors at the beginning and the end
	 * of the array, the array will be left untouched. Any other instances (e.g. in the middle of the array)
	 * will be removed.
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param wsInterceptors the ws interceptors
	 * @param keystore the path to the client ssl keystore
	 * @param keystorePass the pass-word for the client ssl keystore
	 * @param truststore the path to the client ssl truststore
	 * @param truststorePass the pass-word for the client ssl truststore
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SOAPException error creating message factory
	 */
	protected final WebServiceTemplate createSaajSslWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint,  // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout,  // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller,  // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final ClientInterceptor[] wsInterceptors,  // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource keystore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String keystorePass, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource truststore,  // NOSONAR do NOT encapsulate params just to reduce the number
			final String truststorePass) throws SOAPException { // NOSONAR do NOT encapsulate params just to reduce the number
		return this.createWebServiceTemplate(endpoint, readTimeout, connectionTimeout, marshaller, unmarshaller,
				new HttpRequestInterceptor[] { null },
				new HttpResponseInterceptor[] { null }, wsInterceptors,
				new SaajSoapMessageFactory(MessageFactory.newInstance()),
				keystore, keystorePass, truststore, truststorePass);
	}

	/**
	 * Creates web service template using the supplied http request/response interceptors and the provided web service
	 * interceptors and message factory - if web service clients wish to configure their own message factory.
	 *
	 * {@link AuditWsInterceptor} to audit the request and response are added automatically to
	 * the {@code wsInterceptors} array of {@link ClientInterceptor}s.
	 * If the {@code wsInterceptors} array already has AuditWebserviceInterceptors at the beginning and the end
	 * of the array, the array will be left untouched. Any other instances (e.g. in the middle of the array)
	 * will be removed.
	 *
	 *
	 * @param endpoint the endpoint
	 * @param readTimeout the read timeout
	 * @param connectionTimeout the connection timeout
	 * @param marshaller the marshaller
	 * @param unmarshaller the unmarshaller
	 * @param httpRequestInterceptors the http request interceptors
	 * @param httpResponseInterceptors the http response interceptors
	 * @param wsInterceptors the ws interceptors
	 * @param messageFactory webservice message factory
	 * @param truststore the path to the client ssl truststore
	 * @param truststorePass the pass-word for the client ssl truststore
	 * @param keystore the path to the client ssl keystore
	 * @param keystorePass the pass-word for the client ssl keystore
	 * @return the web service template
	 * @throws KeyManagementException the key management exception
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected final WebServiceTemplate createWebServiceTemplate( // NOSONAR do NOT encapsulate params just to reduce the number
			final String endpoint, // NOSONAR do NOT encapsulate params just to reduce the number
			final int readTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final int connectionTimeout, // NOSONAR do NOT encapsulate params just to reduce the number
			final Marshaller marshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final Unmarshaller unmarshaller, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpRequestInterceptor[] httpRequestInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final HttpResponseInterceptor[] httpResponseInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final ClientInterceptor[] wsInterceptors, // NOSONAR do NOT encapsulate params just to reduce the number
			final WebServiceMessageFactory messageFactory, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource keystore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String keystorePass, // NOSONAR do NOT encapsulate params just to reduce the number
			final Resource truststore, // NOSONAR do NOT encapsulate params just to reduce the number
			final String truststorePass) { // NOSONAR do NOT encapsulate params just to reduce the number
		// configure the message sender
		final HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		messageSender.setReadTimeout(readTimeout);
		messageSender.setConnectionTimeout(connectionTimeout);

		final HttpClientBuilder httpClient = HttpClients.custom();

		if (httpRequestInterceptors != null) {
			for (final HttpRequestInterceptor httpRequestInterceptor : httpRequestInterceptors) {
				httpClient.addInterceptorFirst(httpRequestInterceptor);
			}
		}
		if (httpResponseInterceptors != null) {
			for (final HttpResponseInterceptor httpResponseInterceptor : httpResponseInterceptors) {
				httpClient.addInterceptorLast(httpResponseInterceptor);
			}
		}

		addSslContext(httpClient, keystore, keystorePass, truststore, truststorePass);

		LOGGER.debug("HttpClient Object : %s% {}", ReflectionToStringBuilder.toString(httpClient));
		LOGGER.debug("Default Uri : %s% {}", endpoint);

		messageSender.setHttpClient(httpClient.build());

		// set the message factory & configure and return the template
		final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
		webServiceTemplate.setMessageFactory(messageFactory);
		webServiceTemplate.setMessageSender(messageSender);
		webServiceTemplate.setDefaultUri(endpoint);
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(unmarshaller);
		webServiceTemplate.setInterceptors(addAuditLoggingInterceptors(wsInterceptors));
		return webServiceTemplate;
	}

	/**
	 * If keystore and truststore are not null, SSL context is added to the httpClient.
	 *
	 * @param httpClient the http client
	 * @param keystoreResource the keystore resource
	 * @param keystorePass the keystore pass
	 * @param truststore the truststore
	 * @param truststorePass the truststore pass
	 */
	protected void addSslContext(final HttpClientBuilder httpClient,
			final Resource keystoreResource, final String keystorePass, final Resource truststore, final String truststorePass) {

		if ((keystoreResource != null) && (truststore != null)) {
			// Add SSL
			try {
				KeyStore keystore = this.keyStore(keystoreResource, keystorePass.toCharArray());

				SSLContext sslContext =
						SSLContextBuilder.create()
								.loadKeyMaterial(keystore, keystorePass.toCharArray())
								.loadTrustMaterial(truststore.getURL(), truststorePass.toCharArray()).build();
				// use NoopHostnameVerifier to turn off host name verification
				SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
				httpClient.setSSLSocketFactory(csf);

			} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException | IOException
					| UnrecoverableKeyException e) {
				handleExceptions(e);
			}
		}
	}

	private void handleExceptions(final Exception e) {
		MessageKeys key = MessageKeys.BIP_SECURITY_SSL_CONTEXT_FAIL;
		String[] params = new String[] { e.getClass().getSimpleName(), e.getMessage() };
		LOGGER.error(key.getMessage(params), e);
		throw new BipPartnerRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e, params);
	}

	/**
	 * Produce a KeyStore object for a given JKS file and its pass-word.
	 *
	 * @param keystoreResource the keystore resource
	 * @param pass the pass-word
	 * @return KeyStore
	 * @throws KeyStoreException the key store exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private KeyStore keyStore(final Resource keystoreResource, final char[] pass)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance("JKS");

		LOGGER.debug("KeyStore: {}", keyStore);
		LOGGER.debug("Resource: {}", keystoreResource);

		InputStream inputstream = null;
		try {
			inputstream = keystoreResource.getInputStream();
			keyStore.load(inputstream, pass);
			LOGGER.debug("KeyStore load done");
		} finally {
			BaseAsyncAudit.closeInputStreamIfRequired(inputstream);
		}
		return keyStore;
	}

	/**
	 * Adds audit logging interceptors to the {@link ClientInterceptor} array.
	 * <p>
	 * If the {@code wsInterceptor} parameter is NOT null or empty, an audit interceptor
	 * will be added to log BEFORE the other interceptors run (a "raw" log),
	 * and second interceptor to log AFTER the other interceptors run (a "wire" log).
	 *
	 * @param wsInterceptors the ClientInterceptor array being added to the configuration
	 * @return ClientInterceptor[] - the updated array of interceptors
	 */
	@SuppressWarnings("unchecked")
	private ClientInterceptor[] addAuditLoggingInterceptors(final ClientInterceptor[] wsInterceptors) {

		// if no other interceptors run, no need to add "After" audit log
		boolean logAfter = (wsInterceptors != null) && (wsInterceptors.length > 0);
		LOGGER.debug("Initial ClientInterceptors list: " + Arrays.toString(wsInterceptors));

		List<ClientInterceptor> list = new ArrayList<>();

		/* Add audit logging interceptors for Before and After any other interceptors run */
		if (!logAfter) {
			LOGGER.debug("Adding audit interceptor only for " + AuditWsInterceptorConfig.AFTER.name());
			list.add(new AuditWsInterceptor(AuditWsInterceptorConfig.AFTER));
		} else {
			LOGGER.debug("Adding audit interceptor only for both " + AuditWsInterceptorConfig.BEFORE.name()
					+ " and " + AuditWsInterceptorConfig.AFTER.name());
			list.add(new AuditWsInterceptor(AuditWsInterceptorConfig.BEFORE));
			list.addAll(Collections.arrayToList(wsInterceptors));
			list.add(new AuditWsInterceptor(AuditWsInterceptorConfig.AFTER));
		}

		ClientInterceptor[] newWsInterceptors = list.toArray(new ClientInterceptor[list.size()]);
		LOGGER.debug("Final ClientInterceptors list: " + Arrays.toString(newWsInterceptors));
		return newWsInterceptors;
	}

	/**
	 * Gets the bean name auto proxy creator.
	 *
	 * @param beanNames the bean names
	 * @param interceptorNames the interceptor names
	 * @return the bean name auto proxy creator
	 */
	public final BeanNameAutoProxyCreator getBeanNameAutoProxyCreator(final String[] beanNames, final String[] interceptorNames) {
		final BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
		creator.setBeanNames(beanNames);
		creator.setInterceptorNames(interceptorNames);
		return creator;
	}

	/**
	 * Gets the marshaller.
	 *
	 * @param transferPackage the transfer package
	 * @param schemaLocations the schema locations
	 * @param isLogValidationErrors the is log validation errors
	 * @return the marshaller
	 */
	public final Jaxb2Marshaller getMarshaller(final String transferPackage, final Resource[] schemaLocations,
			final boolean isLogValidationErrors) {
		Defense.notNull(transferPackage, "Marshaller transferPackage cannot be null");

		final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setValidationEventHandler(new JaxbLogAndEatValidationEventHandler(isLogValidationErrors));
		marshaller.setContextPath(transferPackage);
		if (schemaLocations != null) {
			marshaller.setSchemas(schemaLocations);
		}
		try {
			marshaller.afterPropertiesSet();
		} catch (final Exception ex) {

			throw new BipPartnerRuntimeException(MessageKeys.BIP_REST_CONFIG_JAXB_MARSHALLER_FAIL,
					MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, ex);
		}
		return marshaller;
	}

	/**
	 * Gets the performance interceptor.
	 *
	 * @param methodWarningThreshhold the method warning threshhold
	 * @return the performance interceptor
	 */
	public final PerformanceLogMethodInterceptor getPerformanceLogMethodInterceptor(final Integer methodWarningThreshhold) {
		final PerformanceLogMethodInterceptor performanceLogMethodInteceptor = new PerformanceLogMethodInterceptor();
		performanceLogMethodInteceptor.setWarningThreshhold(methodWarningThreshhold);
		return performanceLogMethodInteceptor;
	}

	/**
	 * Gets the security interceptor.
	 *
	 * @param username the username
	 * @param pass-word 
	 * @param vaApplicationName the va application name
	 * @param stationId the stationd id
	 * @return the security interceptor
	 */
	protected final VAServiceWss4jSecurityInterceptor getVAServiceWss4jSecurityInterceptor(final String username,
			final String password, final String vaApplicationName, final String stationId) {
		final VAServiceWss4jSecurityInterceptor interceptor = new VAServiceWss4jSecurityInterceptor();
		interceptor.setSecurementActions(WSConstants.USERNAME_TOKEN_LN);
		interceptor.setSecurementUsername(username);
		interceptor.setSecurementPassword(password);
		interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
		interceptor.setVaApplicationName(vaApplicationName);
		interceptor.setSecurementMustUnderstand(false);
		if (!StringUtils.isEmpty(stationId)) {
			interceptor.setStationId(stationId);
		}
		return interceptor;
	}
}