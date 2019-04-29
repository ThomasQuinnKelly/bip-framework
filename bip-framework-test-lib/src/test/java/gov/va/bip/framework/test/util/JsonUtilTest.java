package gov.va.bip.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import com.jayway.jsonpath.PathNotFoundException;

import gov.va.bip.framework.test.util.JsonUtil;

public class JsonUtilTest {

	String json;

	@Before
	public void setup() throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		File file = new File(classLoader.getResource("json/reference.json").getFile());
		json = new String(Files.readAllBytes(file.toPath()));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void utilityClassTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		final Constructor<JsonUtil> constructor = JsonUtil.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw (UnsupportedOperationException) e.getTargetException();
		}
	}

	@Test
	public void test_readFirstName_Success() throws IOException {
		String firstName = JsonUtil.getString(json, "firstName");
		assertThat(firstName, equalTo("JANE"));
	}

	@Test
	public void test_readInt_Success() throws IOException {
		int assuranceLevel = JsonUtil.getInt(json, "assuranceLevel");
		assertThat(assuranceLevel, equalTo(2));
	}

	@Test
	public void test_getObjectAssertNotNull_Success() throws IOException {
		Object firstName = JsonUtil.getObjectAssertNotNull(json, "lastName");
		assertThat(firstName, equalTo("DOE"));
	}

	@Test(expected = PathNotFoundException.class)
	public void test_getObjectAssertIsNull_Success() throws IOException {
		JsonUtil.getString(json, "firstNameDummy");
	}

	@Test
	public void test_getStringAssertNotBlank_Success() throws IOException {
		JsonUtil.getStringAssertNotBlank(json, "firstName");
	}

	@Test
	public void test_getStringAssertBlank_Success() throws IOException {
		JsonUtil.getStringAssertNotBlank(json, "blankKey");
	}

	@Test
	public void test_getStringAssertIsBlank_Success() throws IOException {
		JsonUtil.getStringAssertIsBlank(json, "blankKey");
	}

}
