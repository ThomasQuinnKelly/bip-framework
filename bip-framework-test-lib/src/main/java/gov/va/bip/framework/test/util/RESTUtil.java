package gov.va.bip.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
	protected String jsonText = new String();

	/**
	 * Holds API response object.
	 */
	private ResponseEntity<String> response = null;

	/**
	 * API response status code
	 */
	private int httpResponseCode;

	/**
	 * Spring REST template object to invoke all API calls.
	 */
	private RestTemplate restTemplate;

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
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(final String strRequestFile, final Map<String, String> mapHeader) {
		try {
			requestHeaders.setAll(mapHeader);
			LOGGER.info("Request File {}", strRequestFile);
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("request/" + strRequestFile);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: {}", "request/" + strRequestFile);
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
	 * @throws Exception
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
	 * Invokes REST end point for a GET method using REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String getResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return executeAPI(serviceURL, request, HttpMethod.GET);
	}

	/**
	 * Invokes REST end point for a POST method using REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */

	public String postResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(jsonText, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Invokes REST end point for a PUT method using REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */

	public String putResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return executeAPI(serviceURL, request, HttpMethod.PUT);
	}

	/**
	 * Invokes REST end point for a DELETE method using REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
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
	 * @param request
	 * @param httpMethod
	 * @return
	 */
	private String executeAPI(final String serviceURL, HttpEntity<?> request, HttpMethod httpMethod) {
		try {
			// request.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			response = restTemplate.exchange(serviceURL, httpMethod, request, String.class);
			httpResponseCode = response.getStatusCodeValue();
			return response.getBody();
		} catch (HttpClientErrorException clientError) {
			LOGGER.error("Http client exception is thrown", clientError);
			httpResponseCode = clientError.getRawStatusCode();
			return clientError.getResponseBodyAsString();
		}
	}

	/**
	 * Invokes REST end point for a multipart method using REST Template API and
	 * return response json object.
	 *
	 * @param serviceURL
	 * @return
	 */

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final String submitPayloadPath) {
		try {
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource(DOCUMENTS_FOLDER_NAME + "/" + fileName);
			final URL urlSubmitPayload = RESTUtil.class.getClassLoader()
					.getResource(PAYLOAD_FOLDER_NAME + "/" + submitPayloadPath);
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
	 * @param serviceURL
	 * @param fileName
	 * @param submitPayload
	 * @return
	 */

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final byte[] submitPayload) {
		try {
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource(DOCUMENTS_FOLDER_NAME + "/" + fileName);
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
     * Private method that is invoked by multipart methods. It uses
     * RESTTemplate generic exchange method for multipart HTTP methods.
     * @param serviceURL
     * @param body
     * @return
     */
	private String executeMultipartAPI(String serviceURL, MultiValueMap<String, Object> body) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Loads the KeyStore and password in to rest Template API so all the API's are
	 * SSL enabled.
	 */

	private RestTemplate getRestTemplate() {
		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		RestTemplate apiTemplate = new RestTemplate();
		if (StringUtils.isNotBlank(pathToKeyStore)) {
			KeyStore keyStore = null;
			String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);

			try (FileInputStream instream = new FileInputStream(pathToKeyStore)) {
				keyStore = KeyStore.getInstance("jks");
				keyStore.load(instream, password.toCharArray());
				SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray())
						.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

				SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
						NoopHostnameVerifier.INSTANCE);
				HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
				ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
				apiTemplate = new RestTemplate(requestFactory);
			} catch (Exception e) {
				LOGGER.error("Issue with the certificate or password", e);
			}
		}
		apiTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
		apiTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		apiTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory()));
		return apiTemplate;
	}

	/**
	 * Creates HttpComponentsClientHttpRequestFactory with different settings such
	 * as connectionTimeout, readTimeout
	 * 
	 * @return
	 */
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
		int connectionTimeout = 20000;
		String readTimeout = "30000";
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				getHttpClientBuilder().build());
		clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
		clientHttpRequestFactory.setReadTimeout(Integer.valueOf(readTimeout));
		return clientHttpRequestFactory;
	}

	/**
	 * Creates PoolingHttpClientConnectionManager with various settings.
	 * 
	 * @return
	 */
	private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		String maxTotalPool = "15";
		String defaultMaxPerRoutePool = "5";
		String validateAfterInactivityPool = "5000";
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(); // NOSONAR
																												// CloseableHttpClient#close
																												// should
																												// automatically
		// shut down the connection pool only if exclusively owned by the client
		poolingConnectionManager.setMaxTotal(Integer.valueOf(maxTotalPool));
		poolingConnectionManager.setDefaultMaxPerRoute(Integer.valueOf(defaultMaxPerRoutePool));
		poolingConnectionManager.setValidateAfterInactivity(Integer.valueOf(validateAfterInactivityPool));
		return poolingConnectionManager;
	}

	/**
	 * Creates HttpClientBuilder and sets PoolingHttpClientConnectionManager,
	 * ConnectionConfig
	 * 
	 * @return
	 */
	private HttpClientBuilder getHttpClientBuilder() {
		String connectionBufferSize = "4128";
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setBufferSize(Integer.valueOf(connectionBufferSize)).build();
		HttpClientBuilder clientBuilder = HttpClients.custom();

		clientBuilder.setConnectionManager(getPoolingHttpClientConnectionManager());
		clientBuilder.setDefaultConnectionConfig(connectionConfig);

		return clientBuilder;
	}

	/**
	 * Loads the expected results from source folder and returns as string.
	 *
	 * @param filename
	 * @return
	 */
	public String readExpectedResponse(final String filename) {
		String strExpectedResponse = null;
		try {
			LOGGER.info("Response File: {}", filename);
			final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("response/" + filename);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: {}", "response/" + filename);
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
	 * @return
	 * @throws IOException
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
	 */
	public void validateStatusCode(final int intStatusCode) {
		assertThat(httpResponseCode, equalTo(intStatusCode));

	}

}
