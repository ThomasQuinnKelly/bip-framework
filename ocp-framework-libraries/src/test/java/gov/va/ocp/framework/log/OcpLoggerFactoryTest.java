package gov.va.ocp.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.slf4j.ILoggerFactory;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;

public class OcpLoggerFactoryTest {

	@Test
	public final void testReferenceLoggerFactory() throws NoSuchMethodException, SecurityException {
		Constructor<OcpLoggerFactory> constructor = OcpLoggerFactory.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			assertTrue(InvocationTargetException.class.equals(e.getClass()));
			assertTrue(OcpRuntimeException.class.equals(e.getCause().getClass()));
		}
	}

	@Test
	public final void testGetLoggerClass() {
		OcpLogger logger = OcpLoggerFactory.getLogger(this.getClass());
		assertNotNull(logger);
		assertTrue(logger.getName().equals(this.getClass().getName()));
	}

	@Test
	public final void testGetLoggerString() {
		OcpLogger logger = OcpLoggerFactory.getLogger(this.getClass().getName());
		assertNotNull(logger);
		assertTrue(logger.getName().equals(this.getClass().getName()));
	}

	@Test
	public final void testGetBoundFactory() {
		ILoggerFactory factory = OcpLoggerFactory.getBoundFactory();
		assertNotNull(factory);
	}
}
