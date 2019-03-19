package gov.va.ocp.framework.test.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import gov.va.ocp.framework.test.service.RESTConfigService;

public class RESTConfigServiceTest {
	

	@Before
	public void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field instance = RESTConfigService.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, null);
	}
	
	@Test
	public void test_SetEnvQA_readProperty_Success() {
		System.setProperty("test.env", "qa");
		String propertyValue = RESTConfigService.getInstance().getProperty("test.property.url");
		assertThat(propertyValue, equalTo("http://qa.reference.com:8080"));
	}
	
	@Test
	public void testreadProperty_Success() {
		System.setProperty("test.env", "");
		String propertyValue = RESTConfigService.getInstance().getProperty("test.property.url");
		assertThat(propertyValue, equalTo("http://localhost:8080"));
	}


	@Test
	public void test_SetSystemProperty_readProperty_Success() {
		System.setProperty("test.property.url", "http://dummyurl:8080");
		String propertyValue = RESTConfigService.getInstance().getProperty("test.property.url", true);
		assertThat(propertyValue, equalTo("http://dummyurl:8080"));
	}

}
