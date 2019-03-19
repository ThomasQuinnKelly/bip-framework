package gov.va.ocp.framework.exception.interceptor;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.event.Level;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * This is a configurable interceptor which will catch and translate one exception type into another. This is useful if
 * you wish to &quot;wrap&quot; 3rd party framework exceptions for a &quot;suite&quot; of beans, and/or if you wish to
 * make sure every exception coming from a tier in your application is of a specific exception type.
 *
 * In general, exceptions will be converted to some application specific equivalent.
 *
 *
 * @see org.aopalliance.intercept.MethodInterceptor
 *
 * @author Jon Shrader
 */
public class InterceptingExceptionTranslator implements ThrowsAdvice {

	/** logger for this class. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(InterceptingExceptionTranslator.class);

	/**
	 * A set of packages and/or exception class names we should exclude during translation.
	 */
	private Set<String> exclusionSet;

	/**
	 * The default exception to raise in the event its not a success and also not a mapped exception type.
	 */
	private Class<? extends OcpRuntimeException> defaultExceptionType;

	/**
	 * Log the thrown exception, and rethrow an exception as defined by {@link #setDefaultExceptionType(Class)}.
	 * <p>
	 * DO NOT REMOVE "target" ARGUMENT IN THE METHOD, AS REMOVING IT BREAKS END POINT CALLS
	 *
	 * @param method the method that threw the exception
	 * @param args the parameters to the method
	 * @param target the target of the method
	 * @param throwable the throwable
	 */
	public final void afterThrowing(final Method method, final Object[] args, final Object target, final Throwable throwable) {
		// set default return type if none was specified
		if (defaultExceptionType == null) {
			defaultExceptionType = OcpRuntimeException.class;
		}

		// figure out what exception type should replace throwable
		OcpRuntimeException resolvedRuntimeException = null;

		// skip conversion if the throwable is in the exclusion set
		try {
			// exclusionSet is initialized in BaseWsClientConfig.getInterceptingExceptionTranslator()
			// with value of BaseWsClientConfig.PACKAGE_FRAMEWORK_EXCEPTION
			if (exclusionSet != null
					&& (exclusionSet.contains(throwable.getClass().getPackage().getName())
							|| exclusionSet.contains(throwable.getClass().getName()))) {
				LOGGER.debug("InterceptingExceptionTranslator is configured to ignore exceptions of type ["
						+ throwable.getClass().getName() + "] - not translating this exception.");
				// let the throwable bubble up through the app untouched
				return;

			} else {
				ExceptionHandlingUtils.logException("InterceptingExceptionTranslator", method, args, throwable);
			}

			// figure out what exception type should replace throwable
			resolvedRuntimeException = ExceptionHandlingUtils.resolveRuntimeException(throwable);

		} catch (final Exception e) {
			String msg = "Exception likely due to configuration error, review log/configuration to troubleshoot";
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR), msg, e);
			resolvedRuntimeException = new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (resolvedRuntimeException != null) {
			// override the throwable, and send resolvedRuntimeException instead
			throw resolvedRuntimeException;
		}
		// otherwise allow the original throwable to continue bubbling up
	}

	/**
	 * Sets the default exception type. The exception type must extend {@link OcpRuntimeException}.
	 * <p>
	 * The default exception type is OcpRuntimeException.
	 *
	 * @param defaultExceptionType the defaultExceptionType to set
	 */
	public final void setDefaultExceptionType(final Class<? extends OcpRuntimeException> defaultExceptionType) {
		this.defaultExceptionType = defaultExceptionType;
	}

	/**
	 * Sets the exclusion set. Each element of the set can be a package name
	 * (or some part of a package name),
	 * or the fully qualified name of a class that extends Throwable.
	 * <p>
	 * Otherwise, the exclusion will be ignored.
	 *
	 * @param exclusionSet the exclusionSet to set
	 */
	public final void setExclusionSet(final Set<String> exclusionSet) {
		this.exclusionSet = exclusionSet;
	}

}