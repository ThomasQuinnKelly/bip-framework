package gov.va.bip.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import gov.va.bip.framework.shared.sanitize.Sanitizer;
import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.service.RESTConfigService;

/**
 * It is a wrapper for rest Template API for making HTTP calls, parse JSON and
 * xml responses and status code check.
 *
 * @author sravi
 */

public class RESTUtil {

	/**
	 * Constant for document folder name
	 */
	private static final String DOCUMENTS_FOLDER_NAME = "documents";

	/**
	 * Constant for payload folder name
	 */
	private static final String PAYLOAD_FOLDER_NAME = "payload";

	/**
	 * Constant for submit folder name
	 */
	private static final String SUBMIT_PAYLOAD = "submitPayload";

	/** The Constant COULD_NOT_FIND_PROPERTY_STRING. */
	private static final String COULD_NOT_FIND_PROPERTY_STRING = "Could not find property : ";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);

	/**
	 * stores request headers
	 */
	private MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();

	/**
	 * Holds json that represents header info
	 */
	protected String jsonText = StringUtils.EMPTY;

	/**
	 * API response status code
	 */
	private int httpResponseCode;

	/**
	 * Spring REST template object to invoke all API calls.
	 */
	private RestTemplate restTemplate;

	/**
	 * Spring rest template response http header
	 */
	private HttpHeaders responseHttpHeaders;

	/**
	 * Constructor to initialize objects.
	 */
	public RESTUtil() {
		this.restTemplate = getRestTemplate();
	}

	/**
	 * Reads file content for a given file resource using URL object.
	 *
	 * @param strRequestFile
	 *            the str request file
	 * @param mapHeader
	 *            the map header
	 */
	public void setUpRequest(final String strRequestFile, final Map<String, String> mapHeader) {
		try {
			requestHeaders.setAll(mapHeader);
			LOGGER.info("Request File {}", strRequestFile);
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("request/" + strRequestFile);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: request/{}", strRequestFile);
				throw new BipTestLibRuntimeException("Requested File Doesn't Exist: request/" + strRequestFile);
			} else {
				// Note - Enhance the code so if Header.Accept is xml, then it
				// should use something like convertToXML function
				jsonText = readFile(new File(urlFilePath.toURI()));
			}
		} catch (final URISyntaxException | IOException ex) {
			LOGGER.error("Unable to do setUpRequest", ex);
		}
	}

	/**
	 * Assigns given header object into local header map.
	 *
	 * @param mapHeader
	 *            the map header
	 */
	public void setUpRequest(final Map<String, String> mapHeader) {
		requestHeaders.setAll(mapHeader);
	}

	/**
	 * Gets header object.
	 *
	 * @return mapHeader
	 */
	public MultiValueMap<String, String> getRequest() {
		return requestHeaders;
	}

	/**
	 * Invokes REST end point for a GET method using REST Template API and
	 * return response JSON object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @return the response
	 */
	public String getResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return executeAPI(serviceURL, request, HttpMethod.GET);
	}

	/**
	 * Invokes REST end point for a POST method using REST Template API and
	 * return response JSON object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @return the string
	 */

	public String postResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(jsonText, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Invokes REST end point for a PUT method using REST Template API and
	 * return response JSON object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @return the string
	 */

	public String putResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return executeAPI(serviceURL, request, HttpMethod.PUT);
	}

	/**
	 * Invokes REST end point for a DELETE method using REST Template API and
	 * return response JSON object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @return the string
	 */

	public String deleteResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return executeAPI(serviceURL, request, HttpMethod.DELETE);
	}

	/**
	 * Private method that is invoked by different http methods. It uses
	 * RESTTemplate generic exchange method for various HTTP methods such as
	 * GET,POST,PUT,DELETE
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param request
	 *            the request
	 * @param httpMethod
	 *            the http method
	 * @return the string
	 */
	private String executeAPI(final String serviceURL, final HttpEntity<?> request, final HttpMethod httpMethod) {
		try {
			ResponseEntity<String> response = restTemplate.exchange(serviceURL, httpMethod, request, String.class);
			httpResponseCode = response.getStatusCodeValue();
			responseHttpHeaders = response.getHeaders();
			return response.getBody();
		} catch (HttpClientErrorException clientError) {
			LOGGER.error("Http client exception is thrown", clientError);
			httpResponseCode = clientError.getRawStatusCode();
			responseHttpHeaders = clientError.getResponseHeaders();
			return clientError.getResponseBodyAsString();
		}
	}

	/**
	 * Invokes REST end point for a multipart method using REST Template API and
	 * return response json object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param fileName
	 *            the file name
	 * @param submitPayloadPath
	 *            the submit payload path
	 * @return the string
	 */

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final String submitPayloadPath) {
		try {
			final URL urlFilePath = RESTUtil.class.getClassLoader()
					.getResource(DOCUMENTS_FOLDER_NAME + File.separator + fileName);
			final URL urlSubmitPayload = RESTUtil.class.getClassLoader()
					.getResource(PAYLOAD_FOLDER_NAME + File.separator + submitPayloadPath);
			final File filePath = new File(urlFilePath.toURI());
			final File filePathSubmitPayload = new File(urlSubmitPayload.toURI());
			String submitPayload = FileUtils.readFileToString(filePathSubmitPayload, "UTF-8");
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", filePath);
			body.add(SUBMIT_PAYLOAD, submitPayload);
			return executeMultipartAPI(serviceURL, body);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Invokes REST end point for a multipart method using REST Template API and
	 * return response json object.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param fileName
	 *            the file name
	 * @param submitPayload
	 *            the submit payload
	 * @return the string
	 */

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final byte[] submitPayload) {
		try {
			final URL urlFilePath = RESTUtil.class.getClassLoader()
					.getResource(DOCUMENTS_FOLDER_NAME + File.separator + fileName);
			final File filePath = new File(urlFilePath.toURI());
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", filePath);
			body.add(SUBMIT_PAYLOAD, submitPayload);
			return executeMultipartAPI(serviceURL, body);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * Execute multipart API.
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param body
	 *            the body
	 * @return the string
	 */
	private String executeMultipartAPI(final String serviceURL, final MultiValueMap<String, Object> body) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Loads the KeyStore and password in to rest Template API so all the API's
	 * are SSL enabled.
	 *
	 * @return the rest template
	 */

	private RestTemplate getRestTemplate() {
		// Create a new instance of the {@link RestTemplate} using default
		// settings.
		RestTemplate apiTemplate = new RestTemplate();

		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		String pathToTrustStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.trustStore", true);
		SSLContextBuilder sslContextBuilder = SSLContexts.custom();
		try {
			if (StringUtils.isBlank(pathToKeyStore) && StringUtils.isBlank(pathToTrustStore)) {
				TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
				sslContextBuilder = sslContextBuilder.loadTrustMaterial(null, acceptingTrustStrategy);
			} else {
				sslContextBuilder = loadKeyMaterial(pathToKeyStore, sslContextBuilder);
				sslContextBuilder = loadTrustMaterial(pathToTrustStore, sslContextBuilder);
			}
			SSLContext sslContext = sslContextBuilder.build();
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
					NoopHostnameVerifier.INSTANCE);
			HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			apiTemplate.setRequestFactory(requestFactory);
		} catch (Exception e) {
			LOGGER.error("Issue with the certificate or password", e);
			throw new BipTestLibRuntimeException("Issue with the certificate or password", e);
		}
		apiTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
		apiTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		apiTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory()));

		for (HttpMessageConverter<?> converter : apiTemplate.getMessageConverters()) {
			if (converter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
			}
		}
		return apiTemplate;
	}

	/**
	 * Load key material.
	 *
	 * @param pathToKeyStore
	 *            the path to key store
	 * @param sslContextBuilder
	 *            the ssl context builder
	 * @return the SSL context builder
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyStoreException
	 *             the key store exception
	 * @throws UnrecoverableKeyException
	 *             the unrecoverable key exception
	 * @throws CertificateException
	 *             the certificate exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private SSLContextBuilder loadKeyMaterial(final String pathToKeyStore, final SSLContextBuilder sslContextBuilder)
			throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException,
			IOException {
		if (StringUtils.isNotBlank(pathToKeyStore)) {
			String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);
			if (StringUtils.isBlank(password)) {
				throw new BipTestLibRuntimeException(COULD_NOT_FIND_PROPERTY_STRING + "javax.net.ssl.keyStorePassword");
			}
			return sslContextBuilder.loadKeyMaterial(new File(Sanitizer.safePath(pathToKeyStore)),
					password.toCharArray(), password.toCharArray());
		}
		return sslContextBuilder;
	}

	/**
	 * Load trust material.
	 *
	 * @param pathToTrustStore
	 *            the path to trust store
	 * @param sslContextBuilder
	 *            the ssl context builder
	 * @return the SSL context builder
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyStoreException
	 *             the key store exception
	 * @throws CertificateException
	 *             the certificate exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private SSLContextBuilder loadTrustMaterial(final String pathToTrustStore,
			final SSLContextBuilder sslContextBuilder)
			throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		if (StringUtils.isNotBlank(pathToTrustStore)) {
			String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.trustStorePassword", true);
			if (StringUtils.isBlank(password)) {
				throw new BipTestLibRuntimeException(
						COULD_NOT_FIND_PROPERTY_STRING + "javax.net.ssl.trustStorePassword");
			}
			return sslContextBuilder.loadTrustMaterial(new File(Sanitizer.safePath(pathToTrustStore)),
					password.toCharArray());
		} else {
			return sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		}
	}

	/**
	 * Http components client http request factory.
	 *
	 * @return the HTTP components client request factory
	 */
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
		int connectionTimeout = 20000;
		int readTimeout = 30000;
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				getHttpClientBuilder().build());
		clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
		clientHttpRequestFactory.setReadTimeout(readTimeout);
		return clientHttpRequestFactory;
	}

	/**
	 * Creates PoolingHttpClientConnectionManager with various settings.
	 *
	 * @return the pooling HTTP client connection manager
	 */
	private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		int maxTotalPool = 15;
		int defaultMaxPerRoutePool = 5;
		int validateAfterInactivityPool = 5000;
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(); // NOSONAR
		// CloseableHttpClient#close
		// should
		// automatically
		// shut down the connection pool only if exclusively owned by the client
		poolingConnectionManager.setMaxTotal(maxTotalPool);
		poolingConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoutePool);
		poolingConnectionManager.setValidateAfterInactivity(validateAfterInactivityPool);
		return poolingConnectionManager;
	}

	/**
	 * Creates HttpClientBuilder and sets PoolingHttpClientConnectionManager,
	 * ConnectionConfig.
	 *
	 * @return the HTTP client builder
	 */
	private HttpClientBuilder getHttpClientBuilder() {
		int connectionBufferSize = 4128;
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setBufferSize(connectionBufferSize).build();
		HttpClientBuilder clientBuilder = HttpClients.custom();

		clientBuilder.setConnectionManager(getPoolingHttpClientConnectionManager());
		clientBuilder.setDefaultConnectionConfig(connectionConfig);

		clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true, new ArrayList<>()) {
			@Override
			public boolean retryRequest(final IOException exception, final int executionCount,
					final HttpContext context) {
				LOGGER.info("Retry request, execution count: {}, exception: {}", executionCount, exception);
				if (exception instanceof org.apache.http.NoHttpResponseException) {
					LOGGER.warn("No response from server on " + executionCount + " call");
					return true;
				}
				return super.retryRequest(exception, executionCount, context);
			}

		});

		return clientBuilder;
	}

	/**
	 * Loads the expected results from source folder and returns as string.
	 *
	 * @param filename
	 *            the filename
	 * @return the string
	 */
	public String readExpectedResponse(final String filename) {
		String strExpectedResponse = null;
		try {
			LOGGER.info("Response File: {}", filename);
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("response/" + filename);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: response/{}", filename);
			} else {
				final File strFilePath = new File(urlFilePath.toURI());
				strExpectedResponse = FileUtils.readFileToString(strFilePath, "ASCII");
			}
		} catch (URISyntaxException | IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return strExpectedResponse;
	}

	/**
	 * Utility method to read file. The parameter holds absolute path.
	 *
	 * @param filename
	 *            the filename
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected String readFile(final File filename) throws IOException {
		String content = null;
		final File file = filename;
		FileReader reader = new FileReader(file);
		try {
			final char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
		} finally {
			reader.close();
		}
		return content;

	}

	/**
	 * Asserts the response status code with the given status code.
	 *
	 * @param intStatusCode
	 *            the int status code
	 */
	public void validateStatusCode(final int intStatusCode) {
		assertThat(httpResponseCode, equalTo(intStatusCode));

	}

	/**
	 * Returns response HTTP headers.
	 *
	 * @return the response HTTP headers
	 */
	public HttpHeaders getResponseHttpHeaders() {
		return responseHttpHeaders;
	}

}
