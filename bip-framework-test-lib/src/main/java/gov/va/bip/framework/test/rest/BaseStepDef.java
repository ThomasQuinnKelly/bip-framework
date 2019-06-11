package gov.va.bip.framework.test.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/**
	 * Utility to handle Rest API calls
	 */
	protected RESTUtil resUtil = null;

	/**
	 * Map that holds key value pair for http headers.
	 */
	protected Map<String, String> headerMap = null;

	/**
	 * Holds api response of Rest API call. This is usually json response string.
	 */
	protected String strResponse = null;

	/**
	 * Utility for RESTConfigService to read REST API config
	 */
	protected RESTConfigService restConfig = null;

	/**
	 * Logger object
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepDef.class);
	/**
	 * Constants for JSON response
	 */
	private static final String RESPONSE_PATH = "target/TestResults/Response/";

	/**
	 * Initialize services and utility
	 */

	public void initREST() {
		resUtil = new RESTUtil();
		restConfig = RESTConfigService.getInstance();
	}

	/**
	 * Stores key value pair from cucumber to a map.
	 * 
	 * @param tblHeader
	 */
	public void passHeaderInformation(final Map<String, String> tblHeader) {
		headerMap = new HashMap<>(tblHeader);
	}

	/**
	 * Sets the bearer token and delegates get API call to rest util.
	 * 
	 * @param strURL
	 * @param isAuth
	 */
	public void invokeAPIUsingGet(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingGet(strURL);
	}

	/**
	 * Delegates get API call without bearer token to rest util.
	 * 
	 * @param strURL
	 */

	public void invokeAPIUsingGet(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.getResponse(strURL);
	}

	/**
	 * Sets the bearer token and delegates post API call to rest util.
	 * 
	 * @param strURL
	 * @param isAuth
	 */
	public void invokeAPIUsingPost(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPost(strURL);
	}

	/**
	 * Delegates put API call without bearer token to rest util.
	 * 
	 * @param strURL
	 */
	public void invokeAPIUsingPut(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.putResponse(strURL);
	}

	/**
	 * Sets the bearer token and delegates put API call to rest util.
	 * 
	 * @param strURL
	 * @param isAuth
	 */
	public void invokeAPIUsingPut(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingPut(strURL);
	}

	/**
	 * Delegates post API call without bearer token to rest util.
	 * 
	 * @param strURL
	 */
	public void invokeAPIUsingPost(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponse(strURL);
	}

	/**
	 * Delegates Multipart API call without bearer token to rest util.
	 * 
	 * @param strURL
	 * @param fileName
	 * @param submitPayloadPath
	 */
	public void invokeAPIUsingPostWithMultiPart(final String strURL, final String fileName,
			final String submitPayloadPath) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponseWithMultipart(strURL, fileName, submitPayloadPath);
	}

	/**
	 * Delegates Multipart API call without bearer token to rest util.
	 * 
	 * @param strURL
	 * @param fileName
	 * @param submitPayloadPath
	 */
	public void invokeAPIUsingPostWithMultiPart(final String strURL, final String fileName,
			final byte[] submitPayloadPath) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.postResponseWithMultipart(strURL, fileName, submitPayloadPath);
	}

	/**
	 * Sets the bearer token and delegates delete API call to rest util.
	 * 
	 * @param strURL
	 * @param isAuth
	 */
	public void invokeAPIUsingDelete(final String strURL, final boolean isAuth) {
		if (isAuth) {
			setBearerToken();
		}
		invokeAPIUsingDelete(strURL);
	}

	/**
	 * Delegates delete API call without bearer token to rest util.
	 * 
	 * @param strURL
	 */
	public void invokeAPIUsingDelete(final String strURL) {
		resUtil.setUpRequest(headerMap);
		strResponse = resUtil.deleteResponse(strURL);
	}

	/**
	 * Invokes bearer token service to get token and sets as authorization key in
	 * header map.
	 */
	private void setBearerToken() {
		final String bearerToken = BearerTokenService.getInstance().getBearerToken();
		headerMap.put("Authorization", "Bearer " + bearerToken);
	}

	/**
	 * Validates the status code.
	 * 
	 * @param intStatusCode
	 */
	public void validateStatusCode(final int intStatusCode) {
		resUtil.validateStatusCode(intStatusCode);
	}

	/**
	 * Loads JSON property file that contains header values in to header map. Method
	 * parameter user contains environment and user name delimited by -. The method
	 * parses the environment and user name and loads JSON header file.
	 *
	 * @param user
	 *            Contains the environment and user name delimited by - for eg:
	 *            ci-janedoe
	 * @throws IOException
	 */
	public void setHeader(final String user) throws IOException {
		final Map<String, String> tblHeader = new HashMap<>();

		final String[] values = user.split("-");

		final String env = values[0];
		final String userName = values[1];
		final String url = "users/" + env + "/" + userName + ".properties";
		final Properties properties = new Properties();
		InputStream is = null;
		try {
			is = RESTConfigService.class.getClassLoader().getResourceAsStream(url);
			properties.load(is);
			for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
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
	 * @param strResFile the str res file
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
	 * Does an assertion per line. Reads the expected response file. Loops through
	 * each of this file and does an assertion to see if it exists in the actual
	 * service response.
	 * 
	 * If the actual response contains a lot of information that we can ignore, we
	 * can just target the lines we are concern with.
	 *
	 * @param strResFile the response file string
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
