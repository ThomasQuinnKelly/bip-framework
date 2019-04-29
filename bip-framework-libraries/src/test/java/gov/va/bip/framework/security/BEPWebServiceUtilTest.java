package gov.va.bip.framework.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BEPWebServiceUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrivateConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<BEPWebServiceUtil> c = BEPWebServiceUtil.class.getDeclaredConstructor();
		c.setAccessible(true);
		BEPWebServiceUtil u = c.newInstance();
		assertNotNull(u);
	}

	@Test
	public void testGetExternalUIDWithoutHashGeneration() {
		final String retVal = BEPWebServiceUtil.getExternalUID("UnitTest_EVSS");
		assertNotNull(retVal);
		// assertEquals(true, "UnitTest_EVSS".equals(retVal));

	}

	@Test
	public void testGetExternalUIDWithHashGeneration() {
		final String retVal = BEPWebServiceUtil.getExternalUID("UnitTestEVSSWithStringLengthGreaterThan39");
		assertNotNull(retVal);
		assertFalse("UnitTestEVSSWithStringLengthGreaterThan39".equals(retVal));
	}

	@Test
	public void testGetExternalKey() {
		final String retVal = BEPWebServiceUtil.getExternalKey("UnitTestEVSSKey");
		assertNotNull(retVal);
		// assertTrue("UnitTestEVSSKey".equals(retVal));
	}

	@Test
	public void testGetClientMachine() {
		final String retVal = BEPWebServiceUtil.getClientMachine("localhost");
		assertNotNull(retVal);
	}

	@Test
	public void testGetClientMachineNull() {
		final String retVal = BEPWebServiceUtil.getClientMachine(null);
		assertNotNull(retVal);
	}

	@Test
	public void testLogError() {
		try {
			BEPWebServiceUtil.logError(new Exception("test error message"));
		} catch (Exception e) {
			fail("exception should not be thrown");
		}

	}
}
