package gov.va.ocp.framework.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * Contains utility ops for logging and handling exceptions consistently. Primarily for usage in interceptors which
 * implement ThrowsAdvice and handle exceptions to ensure these all log then consistently.
 *
 * @author jshrader
 */
public final class ExceptionHandlingUtils {
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(ExceptionHandlingUtils.class);

	/** The Constant LOC_EXCEPTION_PREFIX. */
	private static final String LOC_EXCEPTION_PREFIX =
			" caught exception, handling it as configured.  Here are details [";

	/** The Constant LOG_EXCEPTION_MID. */
	private static final String LOG_EXCEPTION_MID = "] args [";

	/** The Constant LOG_EXCEPTION_POSTFIX. */
	private static final String LOG_EXCEPTION_POSTFIX = "].";

	/** The Constant LOG_EXCEPTION_UNDERSCORE. */
	private static final String LOG_EXCEPTION_UNDERSCORE = "_";

	/** The Constant LOG_EXCEPTION_DOT. */
	private static final String LOG_EXCEPTION_DOT = ".";

	/**
	 * private constructor for utility class
	 */
	private ExceptionHandlingUtils() {
	}

	/**
	 * Resolve the throwable to an {@link OcpRuntimeException} (or subclass of OcpRuntimeException).
	 *
	 * @param throwable the throwable
	 * @return the runtime exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static OcpRuntimeException resolveRuntimeException(final Throwable throwable) {
		// custom exception type to represent the error
		OcpRuntimeException resolvedRuntimeException = null;

		if (OcpRuntimeException.class.isAssignableFrom(throwable.getClass())) {
			// have to cast so the "Throwable throwable" variable can be returned as-is
			resolvedRuntimeException = castToOcpRuntimeException(throwable);

		} else if (OcpExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
			resolvedRuntimeException = convertFromOcpExceptionExtender(throwable);

		} else {
			// make a new OcpRuntimeException from the non-OCP throwable
			resolvedRuntimeException =
					new OcpRuntimeException("", throwable.getMessage(), MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
							throwable);
		}

		return resolvedRuntimeException;
	}

	static OcpRuntimeException convertFromOcpExceptionExtender(final Throwable throwable) {
		OcpRuntimeException resolvedRuntimeException = null;
		try {
			// cast "Throwable throwable" variable to the OCP exception interface
			OcpExceptionExtender ocp = (OcpExceptionExtender) throwable;
			// instantiate the Runtime version of the interface
			resolvedRuntimeException = (OcpRuntimeException) throwable.getClass()
					.getConstructor(String.class, String.class, MessageSeverity.class, HttpStatus.class, Throwable.class)
					.newInstance(ocp.getKey(), throwable.getMessage(), ocp.getSeverity(), ocp.getStatus(), throwable);
		} catch (ClassCastException | IllegalAccessException | IllegalArgumentException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			String msg = "Could not instantiate OcpRuntimeException using values from throwable "
					+ throwable.getClass().getName();
			LOGGER.error(new OcpBanner("ResolveRuntimeException Failed", Level.ERROR), msg, e);
			throw new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resolvedRuntimeException;
	}

	static OcpRuntimeException castToOcpRuntimeException(final Throwable throwable) { // method added for testability
		OcpRuntimeException resolvedRuntimeException = null;
		try {
			resolvedRuntimeException = (OcpRuntimeException) throwable;
		} catch (ClassCastException e) {
			String msg = "Could not cast " + throwable.getClass().getName() + " to OcpRuntimeException";
			LOGGER.error(new OcpBanner("ResolveRuntimeException Failed", Level.ERROR), msg, e);
			throw new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resolvedRuntimeException;
	}

	/**
	 * Log exception.
	 *
	 * @param catcher the catcher - some descriptive name for whomever caught this exception and wants it logged
	 * @param method the method
	 * @param args the args
	 * @param throwable the throwable
	 */
	public static void logException(final String catcher, final Method method, final Object[] args,
			final Throwable throwable) {
		final OcpLogger errorLogger =
				OcpLoggerFactory.getLogger(method.getDeclaringClass().getName() + LOG_EXCEPTION_DOT + method.getName()
						+ LOG_EXCEPTION_UNDERSCORE + throwable.getClass().getName());
		final String errorMessage =
				throwable.getClass().getName() + " thrown by " + method.getDeclaringClass().getName()
						+ LOG_EXCEPTION_DOT + method.getName();
		if (errorLogger.isWarnEnabled()) {
			errorLogger.warn(catcher + LOC_EXCEPTION_PREFIX + errorMessage + LOG_EXCEPTION_MID + Arrays.toString(args)
					+ LOG_EXCEPTION_POSTFIX, throwable);
		} else {
			// if we disable warn logging (all the details and including stack trace) we only show minimal
			// evidence of the error in the logs
			errorLogger.error(catcher + LOC_EXCEPTION_PREFIX + errorMessage + LOG_EXCEPTION_MID + Arrays.toString(args)
					+ LOG_EXCEPTION_POSTFIX);
		}
	}

}
