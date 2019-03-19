package gov.va.ocp.framework.test.util;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ocp.framework.test.service.RESTConfigService;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * It is a wrapper for rest assured API for making HTTP calls, parse JSON and
 * xml responses and status code check.
 *
 * @author sravi
 */

public class RESTUtil { 

	private static final String DOCUMENTS_FOLDER_NAME = "documents";
	private static final String PAYLOAD_FOLDER_NAME = "payload";
	private static final String SUBMIT_PAYLOAD = "submitPayload";
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);

	// stores request headers
	private Map<String, String> mapReqHeader = new HashMap<>(); 
	String jsonText = new String();
	Response response = null; // stores response from rest

	/**
	 * Reads file content for a given file resource using URL object.
	 *
	 * @param strRequestFile
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(final String strRequestFile, final Map<String, String> mapHeader) {
		try {
			mapReqHeader = mapHeader;
			if (strRequestFile != null) {
				LOGGER.info("Request File {}", strRequestFile);
				final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("request/" + strRequestFile);
				if (urlFilePath == null) {
					LOGGER.error("Requested File Doesn't Exist: {}", "request/" + strRequestFile);
				} else {
					// Note - Enhance the code so if Header.Accept is xml, then it
					// should use something like convertToXML function
					jsonText = readFile(new File(urlFilePath.toURI()));
				}
			}
		} catch (final URISyntaxException ex) {
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
		mapReqHeader = mapHeader;
	}

	/**
	 * Gets  header object.
	 *
	 * @return mapHeader
	 */
	public Map<String, String> getRequest() {
		return mapReqHeader;
	}
	
	/**
	 * Invokes REST end point for a GET method using REST assured API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String getResponse(final String serviceURL) {
		doWithRetry(() -> given().config(getRestAssuredConfig()).log().all().headers(mapReqHeader).urlEncodingEnabled(false).when().get(serviceURL),5);
		return response.asString();
	}

	/**
	 * Invokes REST end point for a delete method using REST assured API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String deleteResponse(final String serviceURL) {
		doWithRetry(() -> given().config(getRestAssuredConfig()).log().all().headers(mapReqHeader).urlEncodingEnabled(false).when().delete(serviceURL),5);
		return response.asString();
	}

	/**
	 * Invokes REST end point for a Post method using REST assured API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String postResponse(final String serviceURL) {
		doWithRetry(() -> given().config(getRestAssuredConfig()).log().all().headers(mapReqHeader).urlEncodingEnabled(false).body(jsonText).when().post(serviceURL), 5);
		return response.asString();
	}

	/**
	 * Loads the KeyStore and password in to rest assured API so all the API's are SSL enabled.
	 */
	private RestAssuredConfig getRestAssuredConfig() {
		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		if (StringUtils.isBlank(pathToKeyStore)) {
			RestAssured.useRelaxedHTTPSValidation();
		} else {
			KeyStore keyStore = null;
			String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);

			try (FileInputStream instream = new FileInputStream(pathToKeyStore)) {
				keyStore = KeyStore.getInstance("jks");
				keyStore.load(instream, password.toCharArray());
			} catch (Exception e) {
				LOGGER.error("Issue with the certificate or password", e);
			}

			org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory = null;
			SSLConfig config = null;

			try {
				clientAuthFactory = new org.apache.http.conn.ssl.SSLSocketFactory(keyStore, password);
				// set the config in rest assured
				X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				clientAuthFactory.setHostnameVerifier(hostnameVerifier);
				config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();

				return RestAssured.config().sslConfig(config);

			} catch (Exception e) {
				LOGGER.error("Issue while configuring certificate ", e);

			}
		}
		return RestAssured.config();
	}

	/**
	 * Invokes REST end point for a multipart method using REST assured API and
	 * return response json object.
	 *
	 * @param serviceURL
	 * @return
	 */

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final String submitPayloadPath) {
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource(DOCUMENTS_FOLDER_NAME + "/" + fileName);
		final URL urlSubmitPayload = RESTUtil.class.getClassLoader()
				.getResource(PAYLOAD_FOLDER_NAME + "/" + submitPayloadPath);

		try {
			final File filePath = new File(urlFilePath.toURI());
			final File filePathSubmitPayload = new File(urlSubmitPayload.toURI());
			String submitPayload = FileUtils.readFileToString(filePathSubmitPayload, "UTF-8");
			response = requestSpecification.contentType("multipart/form-data").urlEncodingEnabled(false)
					.headers(mapReqHeader).when().multiPart("file", filePath)
					.multiPart(SUBMIT_PAYLOAD, submitPayload, "application/json").post(serviceURL);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);

		}
		return response.asString();

	}

	public String postResponseWithMultipart(final String serviceURL, final String fileName,
			final byte[] submitPayload) {
		RequestSpecification requestSpecification = given();
		if (LOGGER.isDebugEnabled()) {
			requestSpecification = given().log().all();
		}
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource(DOCUMENTS_FOLDER_NAME + "/" + fileName);

		try {
			final File filePath = new File(urlFilePath.toURI());
			response = requestSpecification.contentType("multipart/form-data").urlEncodingEnabled(false)
					.headers(mapReqHeader).when().multiPart("file", filePath)
					.multiPart(SUBMIT_PAYLOAD, SUBMIT_PAYLOAD, submitPayload, "application/json").post(serviceURL);
		} catch (final Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return response.asString();

	}

	/**
	 * Invokes REST end point for a put method using REST assured API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String putResponse(final String serviceURL) {
		doWithRetry(() -> given().log().all().headers(mapReqHeader).urlEncodingEnabled(false).body(jsonText).when()
				.put(serviceURL), 5);

		return response.asString();
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

	protected String readFile(final File filename) {
		String content = null;
		final File file = filename;
		try (FileReader reader = new FileReader(file)) {
			final char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return content;

	}

	/**
	 * Asserts the response status code with the given status code.
	 *
	 * @param intStatusCode
	 */
	public void validateStatusCode(final int intStatusCode) {
		final int actStatusCode = response.getStatusCode();
		assertThat(actStatusCode, equalTo(intStatusCode));
		
	}

	/**
	 * Performs actual execution of given supplier function with retries. The
	 * execution attempts to retry until it reaches the max attempts or successful execution.
	 *
	 * @param supplier
	 * @param attempts
	 */
	public void doWithRetry(Supplier<Response> supplier, int attempts) {
		boolean failed = false;
		int retries = 0;
		do {
			response = supplier.get();
			if (response.getStatusCode() == 404) {
				LOGGER.error("Rest Assured API failed with 404 ");
				failed = true;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.info("While thread was waiting to retry REST call ... " + e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
			}
			retries++;
		} while (failed && retries < attempts);
		LOGGER.info(response.getBody().asString());
	}
}
