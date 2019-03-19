package gov.va.ocp.framework.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * This class wraps the SLF4J logger to add logging enhancements for the platform.
 * <p>
 * If a future upgrade of SLF4J changes the Logger interface, changes will be required in the OcpLogger class.
 *
 * @author aburkholder
 */
public final class OcpLoggerFactory {

	/**
	 * Do not instantiate.
	 */
	private OcpLoggerFactory() {
		throw new OcpRuntimeException("", "OcpLoggerFactory is a static class. Do not instantiate it.",
				MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Gets a SLF4J-compliant logger, enhanced for applications, for the specified class.
	 *
	 * @param clazz the Class for which logging is desired
	 * @return OcpLogger
	 * @see org.slf4j.LoggerFactory#getLogger(Class)
	 */
	public static final OcpLogger getLogger(Class<?> clazz) {
		return OcpLogger.getLogger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Gets a SLF4J-compliant logger, enhanced for applications, for the specified name.
	 *
	 * @param name the name under which logging is desired
	 * @return OcpLogger
	 * @see org.slf4j.LoggerFactory#getLogger(String)
	 */
	public static final OcpLogger getLogger(String name) {
		return OcpLogger.getLogger(LoggerFactory.getLogger(name));
	}

	/**
	 * Get the implementation of the logger factory that is bound to SLF4J, that serves as the basis for OcpLoggerFactory.
	 *
	 * @return ILoggerFactory an instance of the bound factory implementation
	 * @see org.slf4j.LoggerFactory#getILoggerFactory()
	 */
	public static final ILoggerFactory getBoundFactory() {
		return LoggerFactory.getILoggerFactory();
	}

}
