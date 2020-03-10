package gov.va.bip.framework.test.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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

	/**
	 * Constructor to initialize objects.
	 *
	 * @param convertersToBeAdded
	 *            List of HttpMessageConverter
	 */
	public RESTUtil(final List<HttpMessageConverter<?>> convertersToBeAdded) {
		this.restTemplate = getRestTemplate(convertersToBeAdded);
	}

	/**
	 * Constructor to initialize with a {@link DeferredCloseRestTemplate} instead of a {@link RestTemplate}.
	 *
	 * @param convertersToBeAdded
	 * 				List of HttpMessageConverter
	 * @param deferredCloseMediaTypes
	 * 				List of MediaTypes to instantiate the {@link DeferredCloseRestTemplate} with. See documentation in
	 *				{@link DeferredCloseRestTemplate} for more details.
	 */
	public RESTUtil(final List<HttpMessageConverter<?>> convertersToBeAdded, final List<MediaType> deferredCloseMediaTypes) {
		this.restTemplate = getDeferredCloseRestTemplate(convertersToBeAdded, deferredCloseMediaTypes);
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
		HttpEntity<?> request = getHttpRequestEntity(false);
		return executeAPI(serviceURL, request, HttpMethod.GET);
	}

	/**
	 * Invokes REST end point for a GET method using REST Template API and
	 * returns responses as generic object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceURL
	 *            the service URL
	 * @param responseType
	 *            the response type
	 * @return the response
	 */
	public <T extends Object> T getResponse(final String serviceURL, final Class<T> responseType) {
		HttpEntity<?> request = getHttpRequestEntity(false);
		return executeAPI(serviceURL, request, HttpMethod.GET, responseType);
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
		HttpEntity<?> request = getHttpRequestEntity(true);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}

	/**
	 * Invokes REST end point for a POST method using REST Template API and
	 * returns response as generic object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceURL
	 *            the service URL
	 * @param responseType
	 *            the response type
	 * @return the t
	 */
	public <T extends Object> T postResponse(final String serviceURL, final Class<T> responseType) {
		HttpEntity<?> request = getHttpRequestEntity(true);
		return executeAPI(serviceURL, request, HttpMethod.POST, responseType);
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
		HttpEntity<?> request = getHttpRequestEntity(true);
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
		HttpEntity<?> request = getHttpRequestEntity(true);
		return executeAPI(serviceURL, request, HttpMethod.DELETE);
	}

	/**
	 * Gets the HTTP request entity.
	 *
	 * @param includeBody
	 *            boolean to include the entity body
	 * @return the HTTP request entity
	 */
	private HttpEntity<?> getHttpRequestEntity(boolean includeBody) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = null;
		if (includeBody) {
			request = new HttpEntity<>(jsonText, headers);
		} else {
			request = new HttpEntity<>(headers);
		}
		return request;
	}

	/**
	 * Private method that is invoked by different HTTP methods. It uses
	 * RESTTemplate generic exchange method for various HTTP methods such as
	 * GET,POST,PUT,DELETE
	 *
	 * @param serviceURL
	 *            the service URL
	 * @param request
	 *            the entity (headers and/or body) to write to the request
	 * @param httpMethod
	 *            the HTTP method
	 * @return the server response string
	 */
	private String executeAPI(final String serviceURL, final HttpEntity<?> request, final HttpMethod httpMethod) {
		return executeAPI(serviceURL, request, httpMethod, String.class);
	}

	/**
	 * Private method that is invoked by different HTTP methods. It uses
	 * RESTTemplate generic exchange method for various HTTP methods such as
	 * GET,POST,PUT,DELETE. Method p
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceURL
	 *            the service URL
	 * @param request
	 *            the entity (headers and/or body) to write to the request
	 * @param httpMethod
	 *            the HTTP method (GET, POST, etc)
	 * @param responseType
	 *            the class type of the return value
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	private <T extends Object> T executeAPI(final String serviceURL, final HttpEntity<?> request,
			final HttpMethod httpMethod, final Class<T> responseType) {
		try {
			// Http response as ResponseEntity
			ResponseEntity<T> response = restTemplate.exchange(serviceURL, httpMethod, request, responseType);
			httpResponseCode = response.getStatusCodeValue();
			responseHttpHeaders = response.getHeaders();
			return response.getBody();
		} catch (HttpClientErrorException clientError) {
			LOGGER.error("Http client exception is thrown{}", clientError);
			LOGGER.error("Response Body {}", clientError.getResponseBodyAsString());
			httpResponseCode = clientError.getRawStatusCode();
			responseHttpHeaders = clientError.getResponseHeaders();
			if (responseType == String.class) {
				return (T) clientError.getResponseBodyAsString();
			} else {
				return null;
			}
		} catch (HttpServerErrorException serverError) {
			LOGGER.error("Http server exception is thrown {}", serverError);
			LOGGER.error("Response Body {}", serverError.getResponseBodyAsString());
			httpResponseCode = serverError.getRawStatusCode();
			responseHttpHeaders = serverError.getResponseHeaders();
			if (responseType == String.class) {
				return (T) serverError.getResponseBodyAsString();
			} else {
				return null;
			}
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
				processMultiPartPayload(payLoadFileName, body, isPayloadPartPojo, payloadPartKeyName);
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
	 * @param payloadPartKeyName
	 *            the payload request part key
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void processMultiPartPayload(final String payLoadFileName, final MultiValueMap<String, Object> body,
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
	 * @param contentType
	 *            the payload file content type
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
	 * Creates and configures a {@link RestTemplate}.
	 *
	 * @param convertersToBeAdded
	 *            the converters to be added
	 * @return the configured rest template
	 */
	private RestTemplate getRestTemplate(List<HttpMessageConverter<?>> convertersToBeAdded) {
		// Create a new instance of the {@link RestTemplate} using default settings.
		RestTemplate apiTemplate = new RestTemplate();

		configureRestTemplate(apiTemplate, convertersToBeAdded);

		return apiTemplate;
	}

	/**
	 * Creates and configures a {@link DeferredCloseRestTemplate}.
	 *
	 * @param convertersToBeAdded
	 *            the converters to be added
	 * @param deferredCloseMediaTypes
	 *            the MediaTypes with which the {@link DeferredCloseRestTemplate} should be instantiated
	 * @return the configured rest template
	 */
	private DeferredCloseRestTemplate getDeferredCloseRestTemplate(List<HttpMessageConverter<?>> convertersToBeAdded, List<MediaType> deferredCloseMediaTypes) {
		// Create a new instance of the {@link DeferredCloseRestTemplate} using default settings.
		DeferredCloseRestTemplate apiTemplate = new DeferredCloseRestTemplate(deferredCloseMediaTypes);

		configureRestTemplate(apiTemplate, convertersToBeAdded);

		return apiTemplate;
	}

	/**
	 * Configures a {@link RestTemplate} by loading the KeyStore and password in to rest Template API so all the API's
	 * are SSL enabled.
	 * @param restTemplate
	 * 			The {@link RestTemplate} to configure
	 * @param convertersToBeAdded
	 * 			The {@link HttpMessageConverter} to be added to the restTemplate
	 */
	private void configureRestTemplate(RestTemplate restTemplate, List<HttpMessageConverter<?>> convertersToBeAdded) {
		// Configure the handed-in RestTemplate object.
		restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));

		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory()));

		List<HttpMessageConverter<?>> existingConverters = restTemplate.getMessageConverters();

		for (HttpMessageConverter<?> existingConverter : existingConverters) {
			LOGGER.debug("Existing HttpMessageConverter {}", existingConverter);
			if (existingConverter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) existingConverter).setWriteAcceptCharset(false);
			}

		}
		if (convertersToBeAdded != null) {
			for (HttpMessageConverter<?> converterToBeAdded : convertersToBeAdded) {
				if (!existingConverters.contains(converterToBeAdded)) {
					LOGGER.debug("HttpMessageConverter Added {}", converterToBeAdded);
					existingConverters.add(converterToBeAdded);
				}
			}
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
