package gov.va.ocp.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.va.ocp.framework.test.util.JsonUtil;

public class JsonUtilTest {

	String json;
	
	@Before
	public void setup() throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		File file = new File(classLoader.getResource("json/reference.json").getFile());
		json = new String(Files.readAllBytes(file.toPath()));		
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

	@Test
	public void test_getObjectAssertIsNull_Success() throws IOException {
		JsonUtil.getString(json, "firstNameDummy");
	}

	@Test
	public void test_getStringAssertNotBlank_Success() throws IOException {
		JsonUtil.getStringAssertNotBlank(json, "firstName");
	}

	@Test
	public void test_getStringAssertIsBlank_Success() throws IOException {
		JsonUtil.getStringAssertIsBlank(json, "blankKey");
	}

	@Test
	public void test_getMap_Success() throws IOException {
		Map<String, Object> attributes = JsonUtil.getMap(json, "attributes");
		String attributeValue = JsonUtil.getString(attributes, "attribute1");
		int intattributeValue = JsonUtil.getInt(attributes, "intattribute");
		assertThat(attributeValue, equalTo("attributeValue1"));
		assertThat(intattributeValue, equalTo(10));
	}
	
	
	

}
