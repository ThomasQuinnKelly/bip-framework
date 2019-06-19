package gov.va.bip.framework.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipExceptionExtender;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * Contains utility ops for logging and handling exceptions consistently. Primarily for usage in interceptors which
 * implement ThrowsAdvice and handle exceptions to ensure these all log then consistently.
 *
 * @author jshrader
 */
public final class ExceptionHandlingUtils {
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(ExceptionHandlingUtils.class);

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
	 * Resolve the throwable to an {@link BipRuntimeException} (or subclass of BipRuntimeException).
	 *
	 * @param messageKey the message key to use for this type of exception
	 * @param throwable the throwable
	 * @return the runtime exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static BipRuntimeException resolveRuntimeException(final MessageKey messageKey, final Throwable throwable) {
		// custom exception type to represent the error
		BipRuntimeException resolvedRuntimeException = null;

		if (BipRuntimeException.class.isAssignableFrom(throwable.getClass())) {
			// have to cast so the "Throwable throwable" variable can be returned as-is
			resolvedRuntimeException = castToBipRuntimeException(throwable);

		} else if (BipExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
			resolvedRuntimeException = convertFromBipExceptionExtender(throwable);

		} else {
			// make a new BipRuntimeException from the non-BIP throwable
			resolvedRuntimeException =
					new BipRuntimeException(messageKey, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable);
		}

		return resolvedRuntimeException;
	}

	static BipRuntimeException convertFromBipExceptionExtender(final Throwable throwable) {
		BipRuntimeException resolvedRuntimeException = null;
		try {
			// cast "Throwable throwable" variable to the BIP exception interface
			BipExceptionExtender bip = (BipExceptionExtender) throwable;
			String [] stringArray = new String [] {};
			// instantiate the Runtime version of the interface
			resolvedRuntimeException = (BipRuntimeException) throwable.getClass()
					.getConstructor(MessageKey.class, MessageSeverity.class, HttpStatus.class, Throwable.class, stringArray.getClass())
					.newInstance(bip.getExceptionData().getMessageKey(), bip.getExceptionData().getSeverity(),
							bip.getExceptionData().getStatus(), throwable, stringArray);
		} catch (ClassCastException | IllegalAccessException | IllegalArgumentException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			MessageKeys key = MessageKeys.BIP_EXCEPTION_HANDLER_ERROR_VALUES;
			LOGGER.error(new BipBanner(BipConstants.RESOLVE_EXCEPTION, Level.ERROR),
					key.getMessage(e.getClass().getName()), e);
			throw new BipRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		return resolvedRuntimeException;
	}

	static BipRuntimeException castToBipRuntimeException(final Throwable throwable) { // method added for testability
		BipRuntimeException resolvedRuntimeException = null;
		try {
			resolvedRuntimeException = (BipRuntimeException) throwable;
		} catch (ClassCastException e) {
			MessageKeys key = MessageKeys.BIP_EXCEPTION_HANDLER_ERROR_CAST;
			LOGGER.error(new BipBanner(BipConstants.RESOLVE_EXCEPTION, Level.ERROR),
					key.getMessage(throwable.getClass().getName()), e);
			throw new BipRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
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
		final BipLogger errorLogger =
				BipLoggerFactory.getLogger(method.getDeclaringClass().getName() + LOG_EXCEPTION_DOT + method.getName()
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
