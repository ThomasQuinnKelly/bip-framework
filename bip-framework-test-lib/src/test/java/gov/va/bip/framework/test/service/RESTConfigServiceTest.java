package gov.va.bip.framework.test.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.service.RESTConfigService;

public class RESTConfigServiceTest {

	/** The name of the environment in which testing is occurring */
	static final String TEST_ENV = "test.env";

	@Before
	public void setup()
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field instance = RESTConfigService.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, null);
	}

	@Test
	public void test_SetEnvQA_readProperty_Success() {
		System.setProperty(TEST_ENV, "qa");
		String propertyValue = null;
		try {
			propertyValue = RESTConfigService.getInstance().getProperty("test.property.url");
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(propertyValue, equalTo("http://qa.reference.com:8080"));
	}

	@Test
	public void testreadProperty_Success() {
		System.setProperty(TEST_ENV, "");
		String propertyValue = null;
		try {
			propertyValue = RESTConfigService.getInstance().getProperty("test.property.url");
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(propertyValue, equalTo("http://localhost:8080"));
	}

	@Test
	public void test_SetSystemProperty_readProperty_Success() {
		System.setProperty("test.property.url", "http://dummyurl:8080");
		String propertyValue = null;
		try {
			propertyValue = RESTConfigService.getInstance().getProperty("test.property.url", true);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(propertyValue, equalTo("http://dummyurl:8080"));
	}

	@Test
	public void test_getInstance_withNullUrlConfigFile() {
		System.setProperty(TEST_ENV, "invalid_env");
		RESTConfigService.getInstance();
		Field instance = null;
		try {
			instance = RESTConfigService.class.getDeclaredField("instance");
			instance.setAccessible(true);
			RESTConfigService staticInstance = (RESTConfigService) instance.get(null);
			assertEquals(null, ReflectionTestUtils.getField(staticInstance, "prop"));
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			fail("test failed due to exception");
			e.printStackTrace();
		} finally {
			try {
				instance.set(null, null);
				System.setProperty(TEST_ENV, "qa");
				RESTConfigService.getInstance();
				instance = RESTConfigService.class.getDeclaredField("instance");
				instance.setAccessible(true);
				RESTConfigService staticInstance = (RESTConfigService) instance.get(null);
				assertNotEquals(ReflectionTestUtils.getField(staticInstance, "prop"), null);
			} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
				fail("test failed due to exception");
				e.printStackTrace();
			}
		}
	}

}
