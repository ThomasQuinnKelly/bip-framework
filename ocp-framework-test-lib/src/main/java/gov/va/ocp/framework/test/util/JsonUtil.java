package gov.va.ocp.framework.test.util;

import java.util.Map;

import org.junit.Assert;

import io.restassured.path.json.JsonPath;
/**
 * Utility class for parsing JSON.
 * @author sravi
 *
 */
public class JsonUtil {

	private JsonUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static final String getString(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final Integer getInt(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final Map<String, Object> getMap(final String json, final String path) {
		return JsonPath.with(json).get(path);
	}

	public static final String getString(final Map<String, Object> map, final String name) {
		return (String) map.get(name);
	}

	public static final int getInt(final Map<String, Object> map, final String name) {
		return (int) map.get(name);
	}

	public static final Object getObjectAssertNotNull(String jsonRequest, String path) {
		Object value = JsonPath.with(jsonRequest).get(path);
		Assert.assertNotNull("json does not contain: " + path + ".", value);
		return value;
	}

	public static final String getStringAssertNotBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot be blank.", !value.trim().isEmpty());
		return value;
	}

	public static final String getStringAssertIsBlank(String jsonRequest, String path) {
		String value = (String) getObjectAssertNotNull(jsonRequest, path);
		Assert.assertTrue(path + " cannot have a value.", value.trim().isEmpty());
		return value;
	}


}
