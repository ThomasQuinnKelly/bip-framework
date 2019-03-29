package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class OcpExceptionConstantsTest {

	@Test
	public void initializeOcpExceptionConstantsTest() {
		try {
			Constructor<OcpExceptionConstants> constructor = OcpExceptionConstants.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			OcpExceptionConstants ocpExceptionConstants = constructor.newInstance();
			assertNotNull(ocpExceptionConstants);
		} catch (NoSuchMethodException e) {
			fail("Exception not expected");
		} catch (SecurityException e) {
			fail("Exception not expected");
		} catch (InstantiationException e) {
			fail("Exception not expected");
		} catch (IllegalAccessException e) {
			fail("Exception not expected");
		} catch (IllegalArgumentException e) {
			fail("Exception not expected");
		} catch (InvocationTargetException e) {
			fail("Exception not expected");
		}

	}
}
