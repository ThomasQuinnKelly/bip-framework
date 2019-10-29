package gov.va.bip.framework.test.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.Scenario;
import gov.va.bip.framework.test.service.BearerTokenService;
import gov.va.bip.framework.test.service.RESTConfigService;
import gov.va.bip.framework.test.util.RESTUtil;

/**
 * Base class for all step definition.
 * 
 * @author sravi
 */

public class BaseStepDef {

	/** Utility to handle Rest API calls. */
	protected RESTUtil resUtil = null;

	/** Map that holds key value pair for HTTP headers. */
	protected Map<String, String> headerMap = null;

	/**
	 * Holds API response of Rest API call. This is usually JSON response
	 * string.
	 */
	protected String strResponse = null;

	/**
	 * Holds API response of Rest API call. This is usually Object response.
	 */
	protected Object objResponse = null;

	/** Utility for RESTConfigService to read REST API config. */
	protected RESTConfigService restConfig = null;

	/** Logger object. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepDef.class);

	/** Constants for JSON response. */
	private static final String RESPONSE_PATH = "target/TestResults/Response/";

	/**
	 * Initialize RESTUtil and RESTConfigService instances
	 */
	public void initREST() {
		initREST(new ArrayList<>());
	}

	/**
	 * Initialize RESTUtil with custom HttpMessageConverter and
	 * RESTConfigService instance
	 *
	 * @param messageConverters
	 *            the HttpMessageConverter
	 */
	public void initREST(List<HttpMessageConverter<?>> messageConverters) {
		resUtil = new RESTUtil(messageConverters);
		restConfig = RESTConfigService.getInstance();
	}

	/**
	 * Stores key value pair from cucumber to a map.
	 *
	 * @param tblHeader
	 *            the data table header
	 */
	public void passHeaderInformation(final Map<String, String> tblHeader) {
		headerMap = new HashMap<>(tblHeader);
	}

	/**
	 * Delegates GET API call without bearer token to rest utility method.
	 * 
	 * This method populates String API response
	 * 
	 * @param serviceUrl
	 *            the service URL
	 */

	public void invokeAPIUsingGet(final String serviceUrl) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.getResponse(serviceUrl);
	}

	/**
	 * Sets the bearer token and delegates GET API call to rest utility method.
	 * 
	 * This method populates String API response
	 *
	 * @param serviceUrl
	 *            the service URL
	 * @param isAuth
	 *            boolean if set to TRUE would invoke bearer token service to
	 *            get token and sets Authorization key in header map
	 */
	public void invokeAPIUsingGet(final String serviceUrl, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingGet(serviceUrl);
	}

	/**
	 * Delegates GET API call without bearer token to rest utility method.
	 * 
	 * This method populates Object API response
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceUrl
	 *            the service URL
	 * @param responseType
	 *            the response type
	 */

	public <T extends Object> void invokeAPIUsingGet(final String serviceUrl, final Class<T> responseType) {
		resUtil.setUpRequest(headerMap);
		objResponse = resUtil.getResponse(serviceUrl, responseType);
	}

	/**
	 * Sets the bearer token and delegates GET API call to rest utility method.
	 * 
	 * This method populates Object API response
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceUrl
	 *            the service URL
	 * @param isAuth
	 *            boolean if set to TRUE would invoke bearer token service to
	 *            get token and sets Authorization key in header map
	 * @param responseType
	 *            the response type
	 */

	public <T extends Object> void invokeAPIUsingGet(final String serviceUrl, final boolean isAuth,
			final Class<T> responseType) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingGet(serviceUrl, responseType);
	}

	/**
	 * Delegates POST API call without bearer token to rest utility method.
	 * 
	 * This method populates String API response
	 *
	 * @param serviceUrl
	 *            the service URL
	 */
	public void invokeAPIUsingPost(final String serviceUrl) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponse(serviceUrl);
	}

	/**
	 * Sets the bearer token and delegates post API call to rest utility method.
	 *
	 * This method populates String API response
	 * 
	 * @param serviceUrl
	 *            the service url
	 * @param isAuth
	 *            boolean if set to TRUE would invoke bearer token service to
	 *            get token and sets Authorization key in header map
	 */
	public void invokeAPIUsingPost(final String serviceUrl, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPost(serviceUrl);
	}

	/**
	 * Delegates post API call without bearer token to rest utility method.
	 * 
	 * This method populates Object API response
	 *
	 * @param <T>
	 *            the generic type
	 * @param serviceUrl
	 *            the service url
	 * @param responseType
	 *            the response type
	 */
	public <T extends Object> void invokeAPIUsingPost(final String serviceUrl, final Class<T> responseType) {
		resUtil.setUpRequest(headerMap);
		objResponse = resUtil.postResponse(serviceUrl, responseType);
	}

	/**
	 * Sets the bearer token and delegates post API call to rest utility method.
	 *
	 * This method populates Object API response
	 * 
	 * @param <T>
	 *            the generic type
	 * @param serviceUrl
	 *            the service url
	 * @param isAuth
	 *            boolean if set to TRUE would invoke bearer token service to
	 *            get token and sets Authorization key in header map
	 * @param responseType
	 *            the response type
	 */
	public <T extends Object> void invokeAPIUsingPost(final String serviceUrl, final boolean isAuth,
			final Class<T> responseType) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPost(serviceUrl, responseType);
	}

	/**
	 * Delegates MultiPart API POST method call to an end point that consumes
	 * "multipart/form-data". <br/>
	 * <br/>
	 * Document file to be copied under <b>src/resources/documents</b> directory
	 * and Payload file under <b>src/resources/payload</b> directory of
	 * *-inttest project <br/>
	 * 
	 * @param serviceUrl
	 *            the service end point absolute URL
	 * @param documentFileName
	 *            the document file name to be uploaded
	 * @param payLoadFileName
	 *            the pay load file name for the uploaded document
	 */
	public void invokeAPIUsingPostWithMultiPart(final String serviceUrl, final String documentFileName,
			final String payLoadFileName) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponseWithMultipart(serviceUrl, documentFileName, payLoadFileName, Boolean.FALSE,
				StringUtils.EMPTY);
	}

	/**
	 * Delegates MultiPart API POST method call to an end point that consumes
	 * "multipart/form-data". Also supports passing boolean if payload part is
	 * POJO/JSON along with the key name. If part key name isn't set, then the
	 * library uses file name as the default key<br/>
	 * <br/>
	 * Document file to be copied under <b>src/resources/documents</b> directory
	 * and Payload file under <b>src/resources/payload</b> directory of
	 * *-inttest project <br/>
	 *
	 * @param serviceUrl
	 *            the service end point absolute URL
	 * @param documentFileName
	 *            the document file name to be uploaded
	 * @param payLoadFileName
	 *            the pay load file name for the uploaded document
	 * @param isPayloadPartPojo
	 *            boolean to be set to TRUE if the payload is POJO/JSON
	 * @param payloadPartKeyName
	 *            the payload part key name to be set
	 */
	public void invokeAPIUsingPostWithMultiPart(final String serviceUrl, final String documentFileName,
			final String payLoadFileName, final Boolean isPayloadPartPojo, final String payloadPartKeyName) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponseWithMultipart(serviceUrl, documentFileName, payLoadFileName,
				isPayloadPartPojo, payloadPartKeyName);
	}

	/**
	 * Delegates PUT API call without bearer token to rest utility method.
	 * 
	 * This method populates String API response
	 * 
	 * @param serviceUrl
	 */
	public void invokeAPIUsingPut(final String serviceUrl) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.putResponse(serviceUrl);
	}

	/**
	 * Sets the bearer token and delegates PUT API call to rest utility method.
	 *
	 * This method populates String API response
	 * 
	 * @param serviceUrl
	 *            the service URL
	 * @param isAuth
	 *            boolean if set to TRUE would invoke bearer token service to
	 *            get token and sets Authorization key in header map
	 */
	public void invokeAPIUsingPut(final String serviceUrl, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPut(serviceUrl);
	}

	/**
	 * Delegates DELETE API call without bearer token to rest utility method.
	 * 
	 * This method populates String API response
	 *
	 * @param strURL
	 *            the str URL
	 */
	public void invokeAPIUsingDelete(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.deleteResponse(strURL);
	}

	/**
	 * Sets the bearer token and delegates DELETE API call to rest utility
	 * method.
	 * 
	 * This method populates String API response
	 *
	 * @param strURL
	 *            the str URL
	 * @param isAuth
	 *            the is auth
	 */
	public void invokeAPIUsingDelete(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingDelete(strURL);
	}

	/**
	 * Invokes bearer token service to get token and sets as authorization key
	 * in header map.
	 */
	private void setBearerToken() {
		final String bearerToken = BearerTokenService.getInstance().getBearerToken();
		headerMap.put("Authorization", "Bearer " + bearerToken);
	}

	/**
	 * Validates the status code.
	 *
	 * @param intStatusCode
	 *            the status code
	 */
	public void validateStatusCode(final int intStatusCode) {
		resUtil.validateStatusCode(intStatusCode);
	}

	/**
	 * Loads JSON property file that contains header values in to header map.
	 * Method parameter user contains environment and user name delimited by -.
	 * The method parses the environment and user name and loads JSON header
	 * file.
	 *
	 * @param user
	 *            Contains the environment and user name delimited by - for eg:
	 *            ci-janedoe
	 * @throws IOException
	 */
	public void setHeader(final String user) throws IOException {
		LOGGER.debug("User: {}", user);
		final Map<String, String> tblHeader = new HashMap<>();

		final String[] values = user.split("-", 2);

		final String env = values[0];
		final String userName = values[1];
		final String url = "users/" + env + "/" + userName + ".properties";
		LOGGER.debug("Users Properties File: {}", url);
		final Properties properties = new Properties();
		InputStream is = null;
		try {
			is = RESTConfigService.class.getClassLoader().getResourceAsStream(url);
			properties.load(is);
			for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
				LOGGER.debug("Header Key: {}", entry.getKey());
				LOGGER.debug("Header Value: {}", entry.getValue());
				tblHeader.put((String) entry.getKey(), (String) entry.getValue());
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}

		passHeaderInformation(tblHeader);
	}

	/**
	 * Compares REST API call response with given string.
	 *
	 * @param strResFile
	 *            the str res file
	 * @return true, if successful
	 */
	public boolean compareExpectedResponseWithActual(final String strResFile) {
		boolean isMatch = false;
		try {
			final String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
			final ObjectMapper mapper = new ObjectMapper();
			final Object strExpectedResponseJson = mapper.readValue(strExpectedResponse, Object.class);
			final String prettyStrExpectedResponse = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(strExpectedResponseJson);
			final Object strResponseJson = mapper.readValue(strResponse, Object.class);
			final String prettyStrResponseJson = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(strResponseJson);
			isMatch = prettyStrResponseJson.contains(prettyStrExpectedResponse);
			Assert.assertEquals(
					"Actual and expected response are not equal -" + "Actual Response" + prettyStrResponseJson
					+ "\n Expected Response" + prettyStrExpectedResponse,
					prettyStrResponseJson, prettyStrExpectedResponse);
		} catch (final IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
		}
		return isMatch;
	}

	/**
	 * Does an assertion per line. Reads the expected response file. Loops
	 * through each of this file and does an assertion to see if it exists in
	 * the actual service response.
	 * 
	 * If the actual response contains a lot of information that we can ignore,
	 * we can just target the lines we are concern with.
	 *
	 * @param strResFile
	 *            the response file string
	 * @return true, if successful
	 */
	public boolean compareExpectedResponseWithActualByRow(final String strResFile) {
		final String strExpectedResponse = resUtil.readExpectedResponse(strResFile);
		final StringTokenizer tokenizer = new StringTokenizer(strExpectedResponse, "\n");
		while (tokenizer.hasMoreTokens()) {
			final String responseLine = tokenizer.nextToken();
			if (!strResponse.contains(responseLine)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Writes the response to the target folder.
	 *
	 * @param scenario
	 *            the scenario
	 */
	public void postProcess(final Scenario scenario) {
		String strResponseFile = null;
		try {
			strResponseFile = RESPONSE_PATH + scenario.getName() + ".Response";
			FileUtils.writeStringToFile(new File(strResponseFile), strResponse, StandardCharsets.UTF_8);
		} catch (final Exception ex) {
			LOGGER.error("Failed:Unable to write response to a file", ex);

		}
		scenario.write(scenario.getStatus());
	}

}
