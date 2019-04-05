package gov.va.bip.framework.test.util;

import org.junit.Assert;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * Utility class for parsing JSON.
 * @author sravi
 *
 */
public class JsonUtil {

	private JsonUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	private static DocumentContext getDocumentContext(String json) {
		return JsonPath.parse(json);
	}
	
	public static final String getString(final String json, final String path) {
		return getDocumentContext(json).read(path);
		
	}

	public static final Integer getInt(final String json, final String path) {
		return getDocumentContext(json).read(path);
	}

	public static final Object getObjectAssertNotNull(String jsonRequest, String path) {
		Object value = getDocumentContext(jsonRequest).read(path);
		Assert.assertNotNull("json does not contain: " + path + ".", value);
		return value;
	}

	public static final String getStringAssertNotBlank(String jsonRequest, String path) {
		String value = getDocumentContext(jsonRequest).read(path);
		Assert.assertTrue(path + " cannot be blank.", !value.trim().isEmpty());
		return value;
	}

	public static final String getStringAssertIsBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot have a value.", value.trim().isEmpty());
		return value;
	}


}
