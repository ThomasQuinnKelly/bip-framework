package gov.va.bip.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	/** Constant for document folder name. */
	private static final String DOCUMENTS_FOLDER_NAME = "documents";

	/** Constant for payload folder name. */
	private static final String PAYLOAD_FOLDER_NAME = "payload";

	/** The Constant COULD_NOT_FIND_PROPERTY_STRING. */
	private static final String COULD_NOT_FIND_PROPERTY_STRING = "Could not find property : ";

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);

	/** stores request headers. */
	private MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();

	/** Holds json that represents header info. */
	protected String jsonText = StringUtils.EMPTY;

	/** API response status code. */
	private int httpResponseCode;

	/** Spring REST template object to invoke all API calls. */
	private RestTemplate restTemplate;

	/** Spring rest template response http header. */
	private HttpHeaders responseHttpHeaders;

	/** The tika. */
	private Tika tika = new Tika();

	/** Constructor to initialize objects. */
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
			LOGGER.error("Unable to set up request {}", ex);
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
	 * Private method that is invoked by different HTTP methods. It uses
	 * RESTTemplate generic exchange method for various HTTP methods such as
	 * GET,POST,PUT,DELETE
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param request
	 *            the request
	 * @param httpMethod
	 *            the HTTP method
	 * @return the server response string
	 */
	private String executeAPI(final String serviceURL, final HttpEntity<?> request, final HttpMethod httpMethod) {
		try {
			// Http response as ResponseEntity
			ResponseEntity<String> response = restTemplate.exchange(serviceURL, httpMethod, request, String.class);
			httpResponseCode = response.getStatusCodeValue();
			responseHttpHeaders = response.getHeaders();
			return response.getBody();
		} catch (HttpClientErrorException clientError) {
			LOGGER.error("Http client exception is thrown{}", clientError);
			LOGGER.error("Response Body {}", clientError.getResponseBodyAsString());
			httpResponseCode = clientError.getRawStatusCode();
			responseHttpHeaders = clientError.getResponseHeaders();
			return clientError.getResponseBodyAsString();
		} catch (HttpServerErrorException serverError) {
			LOGGER.error("Http server exception is thrown {}", serverError);
			LOGGER.error("Response Body {}", serverError.getResponseBodyAsString());
			httpResponseCode = serverError.getRawStatusCode();
			responseHttpHeaders = serverError.getResponseHeaders();
			return serverError.getResponseBodyAsString();
		}
	}

	/**
	 * Invokes REST end point for a MultiPart method using REST Template API and
	 * return response JSON object.
	 * 
	 * Delegates MultiPart API POST method call to an end point that consumes
	 * "multipart/form-data". <br/>
	 * <br/>
	 * Document file to be copied under <b>src/resources/documents</b> directory
	 * and Payload file under <b>src/resources/payload</b> directory of
	 * *-inttest project <br/>
	 * 
	 * This method expects file names to include the extension, for example:
	 * sample_file.txt or payload.json
	 *
	 * @param serviceURL
	 *            the Service End Point URL
	 * @param documentFileName
	 *            the MultiPart document upload file name
	 * @param payLoadFileName
	 *            the MultiPart document PayLoad file name
	 * @param isPayloadPartPojo
	 *            boolean to determine if the PayLoad is a POJO model
	 * @param payloadPartKeyName
	 *            the PayLoad request part key name
	 * @return the response string
	 */

	public String postResponseWithMultipart(final String serviceURL, final String documentFileName,
			final String payLoadFileName, final Boolean isPayloadPartPojo, final String payloadPartKeyName) {
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			if (StringUtils.isNotEmpty(documentFileName)) {
				// Process Document Set Up
				processMultiPartDocument(documentFileName, body);
			}
			if (StringUtils.isNotEmpty(payLoadFileName)) {
				// Process PayLoad for the Document
				processMultipPartPayload(payLoadFileName, body, isPayloadPartPojo, payloadPartKeyName);
			}
			// return the response
			return postResponseWithMultipart(serviceURL, body, MediaType.MULTIPART_FORM_DATA);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Execute MultiPart API given the service absolute URL, MultiValueMap as
	 * the body and MediaType for execution. For no media type set, defaults to
	 * "multipart/form-data"
	 *
	 * @param serviceURL
	 *            the Service Absolute URL
	 * @param body
	 *            the body of type MultiValueMap
	 * @param mediaType
	 *            the media type MediaType
	 * @return the string
	 */
	public String postResponseWithMultipart(final String serviceURL, final MultiValueMap<String, Object> body,
			final MediaType mediaType) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		headers.setContentType(mediaType == null ? MediaType.MULTIPART_FORM_DATA : mediaType);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Process MultiPart document.
	 *
	 * @param documentFileName
	 *            the MultiPart document file name
	 * @param body
	 *            the body
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void processMultiPartDocument(final String documentFileName, final MultiValueMap<String, Object> body)
			throws IOException {

		final URL documentUrl = RESTUtil.class.getClassLoader()
				.getResource(DOCUMENTS_FOLDER_NAME + File.separator + documentFileName);
		if (documentUrl != null) {
			final byte[] readBytes = IOUtils.toByteArray(documentUrl);
			ByteArrayResource resource = new ByteArrayResource(readBytes) {
				@Override
				public String getFilename() {
					return FilenameUtils.getBaseName(documentUrl.getPath());
				}
			};
			body.add("file", resource);
		}
	}

	/**
	 * Process MultiPart PayLoad.
	 *
	 * @param payLoadFileName
	 *            the multi part pay load file name
	 * @param body
	 *            the body
	 * @param isPayloadPartPojo
	 *            boolean if payload request part as POJO/JSON
	 * @param payloadRequestPartKey
	 *            the payload request part key
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void processMultipPartPayload(final String payLoadFileName, final MultiValueMap<String, Object> body,
			final Boolean isPayloadPartPojo, final String payloadPartKeyName) throws URISyntaxException, IOException {
		final URL payloadUrl = RESTUtil.class.getClassLoader()
				.getResource(PAYLOAD_FOLDER_NAME + File.separator + payLoadFileName);
		LOGGER.debug("Payload Url: {}", payloadUrl);
		if (payloadUrl != null) {
			final File payloadFile = new File(payloadUrl.toURI());
			LOGGER.debug("Payload File: {}", payloadFile);
			final String payloadFileString = FileUtils.readFileToString(payloadFile, Charset.defaultCharset());
			LOGGER.debug("Payload File Content: {}", payloadFileString);
			if (payloadFileString != null) {
				final String fileExtension = FilenameUtils.getExtension(payloadFile.getPath());
				final String baseName = FilenameUtils.getBaseName(payloadFile.getName());
				String contentType = tika.detect(payloadFile);
				LOGGER.debug("Payload Mime Type {}", contentType);
				LOGGER.debug("Payload File Extension {}", fileExtension);
				if (isPayloadPartPojo) {
					final String payloadKey = StringUtils.isNotEmpty(payloadPartKeyName) ? payloadPartKeyName
							: baseName;
					LOGGER.debug("Payload Key Name {}", payloadKey);
					LOGGER.debug("Payload Value {}", payloadFileString);
					HttpHeaders partHeaders = new HttpHeaders();
					partHeaders.setContentType(MediaType.valueOf(contentType));
					HttpEntity<Object> payloadPart = new HttpEntity<>(payloadFileString, partHeaders);
					body.add(payloadKey, payloadPart);
				} else {
					processMultiPartPayloadRequestParam(body, contentType, payloadFileString);
				}
			}
		}
	}

	/**
	 * Process multi part payload request param.
	 *
	 * @param body
	 *            the body
	 * @param payloadFile
	 *            the payload file
	 * @param payloadFileString
	 *            the payload file string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void processMultiPartPayloadRequestParam(final MultiValueMap<String, Object> body, final String contentType,
			final String payloadFileString) throws IOException {
		if (contentType != null && MediaType.valueOf(contentType).includes(MediaType.APPLICATION_JSON)) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(payloadFileString);
			if (root != null) {
				Map<String, String> jsonNodeMap = mapper.convertValue(root,
						new TypeReference<HashMap<String, String>>() {
						});
				for (Map.Entry<String, String> entry : jsonNodeMap.entrySet()) {
					final String requestParamkey = entry.getKey();
					final String requestParamValue = entry.getValue();
					body.add(requestParamkey, requestParamValue);
				}
			}
		} else {
			LOGGER.warn(
					"Payload file doesn't have extension as JSON, hence not setting as request parameters. Mime Type {}",
					contentType);
		}
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
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
					httpClient);
			requestFactory.setBufferRequestBody(false);
			apiTemplate.setRequestFactory(requestFactory);
		} catch (Exception e) {
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
					LOGGER.warn("No response from server on {} call", executionCount);
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
				strExpectedResponse = FileUtils.readFileToString(strFilePath, StandardCharsets.US_ASCII);
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
