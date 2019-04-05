package gov.va.bip.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.slf4j.ILoggerFactory;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

public class BipLoggerFactoryTest {

	@Test
	public final void testReferenceLoggerFactory() throws NoSuchMethodException, SecurityException {
		Constructor<BipLoggerFactory> constructor = BipLoggerFactory.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			assertTrue(InvocationTargetException.class.equals(e.getClass()));
		}
	}

	@Test
	public final void testGetLoggerClass() {
		BipLogger logger = BipLoggerFactory.getLogger(this.getClass());
		assertNotNull(logger);
		assertTrue(logger.getName().equals(this.getClass().getName()));
	}

	@Test
	public final void testGetLoggerString() {
		BipLogger logger = BipLoggerFactory.getLogger(this.getClass().getName());
		assertNotNull(logger);
		assertTrue(logger.getName().equals(this.getClass().getName()));
	}

	@Test
	public final void testGetBoundFactory() {
		ILoggerFactory factory = BipLoggerFactory.getBoundFactory();
		assertNotNull(factory);
	}
}
