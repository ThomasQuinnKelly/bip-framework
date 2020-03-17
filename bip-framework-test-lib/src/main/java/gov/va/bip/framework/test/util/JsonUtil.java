package gov.va.bip.framework.test.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;

/**
 * Utility class for working with JSON data.
 * Services and other objects use this as an utility to manipulate json document.
 * 
 * @author sravi
 *
 */
public class JsonUtil {

	/**
	 * Constructor should be private so not to instantiate this class.
	 */
	private JsonUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Helper method to parse json document and returns DocumentContext.
	 * This performs the check by parsing the JSON.
	 * Json document is passed as string parameter 
	 *
	 * @param json the json
	 * @return the document context
	 */
	private static DocumentContext getDocumentContext(String json) {
		return JsonPath.parse(json);
	}

	/**
	 * Helper method to parse json document, extract particular node by navigating to path.
	 * Returns extracted value as string
	 *
	 * @param json the json
	 * @param path the path
	 * @return the string
	 */
	public static final String getString(final String json, final String path) {
		return getDocumentContext(json).read(path);

	}

	/**
	 * Helper method to parse json document, extract particular node by navigating to path.
	 * Returns extracted value as int
	 *
	 * @param json the json
	 * @param path the path
	 * @return the int
	 */
	public static final Integer getInt(final String json, final String path) {
		return getDocumentContext(json).read(path);
	}

	/**
	 * Helper method to parse json document, extract particular node value by navigating to path and 
	 * finally checks for null value.
	 *
	 * @param jsonRequest the json request
	 * @param path the path
	 * @return the object assert not null
	 */
	public static final Object getObjectAssertNotNull(String jsonRequest, String path) {
		Object value = getDocumentContext(jsonRequest).read(path);
		Assert.assertNotNull("json does not contain: " + path + ".", value);
		return value;
	}

	/**
	 * Helper method to parse json document, extract particular node by navigating to path.
	 * It navigates by given json path and returns output value.
	 *
	 * @param jsonRequest the json request
	 * @param path the path
	 * @return the string assert not blank
	 */
	public static final String getStringAssertNotBlank(String jsonRequest, String path) {
		return getDocumentContext(jsonRequest).read(path);
	}

	/**
	 * Helper method to parse json document, extract particular node by navigating to path.
	 * It navigates by given json path and checks the output is not empty
	 *
	 * @param jsonRequest the json request
	 * @param path the path
	 * @return the string assert is blank
	 */
	public static final String getStringAssertIsBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot have a value.", value.trim().isEmpty());
		return value;
	}

}
