package gov.va.bip.framework.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public final class BipLoggerFactory {

	/**
	 * Do not instantiate.
	 */
	private BipLoggerFactory() {
		throw new RuntimeException("BipLoggerFactory is a static class. Do not instantiate it.");
	}

	/**
	 * Gets a SLF4J-compliant logger, enhanced for Bip applications, for the specified class.
	 *
	 * @param clazz the Class for which logging is desired
	 * @return BipLogger
	 * @see org.slf4j.LoggerFactory#getLogger(Class)
	 */
	public static final BipLogger getLogger(Class<?> clazz) {
		return BipLogger.getLogger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Gets a SLF4J-compliant logger, enhanced for Bip applications, for the specified name.
	 *
	 * @param name the name under which logging is desired
	 * @return BipLogger
	 * @see org.slf4j.LoggerFactory#getLogger(String)
	 */
	public static final BipLogger getLogger(String name) {
		return BipLogger.getLogger(LoggerFactory.getLogger(name));
	}

	/**
	 * Get the implementation of the logger factory that is bound to SLF4J, that serves as the basis for BipLoggerFactory.
	 *
	 * @return ILoggerFactory an instance of the bound factory implementation
	 * @see org.slf4j.LoggerFactory#getILoggerFactory()
	 */
	public static final ILoggerFactory getBoundFactory() {
		return LoggerFactory.getILoggerFactory();
	}

}
