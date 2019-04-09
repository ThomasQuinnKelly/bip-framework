package gov.va.bip.framework.cache.interceptor;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.AuditLogger;
import gov.va.bip.framework.audit.ResponseAuditData;
import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * Audit cache GET operations.
 * <p>
 * This interceptor is equivalent to an Around aspect of the method that
 * has the Cache annotation(s) - e.g. @CachePut.
 * <p>
 * This interceptor does not distinguish cache operations, so all executions
 * of the application caching method will create audit records.
 * If this behavior is undesirable, it will be necessary to override enough
 * of the inherited code to have control of the {@link #doGet(Cache, Object)} method.
 *
 * @author aburkholder
 */
public class BipCacheInterceptor extends CacheInterceptor {
	private static final long serialVersionUID = -7142978196480878033L;

	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheInterceptor.class);
	/**  */
	private static final String ADVICE_NAME = "invokeBipCacheInterceptor";
	private static final String ACTIVITY = "cacheInvoke";

	@Autowired
	transient MessageSource messageSource;

	/** The {@link AuditLogSerializer} for async logging */
	@Autowired
	AuditLogSerializer asyncLogging;

	/**
	 * Instantiate an BipCacheInterceptor.
	 */
	public BipCacheInterceptor() {
		LOGGER.debug("Instantiating " + BipCacheInterceptor.class.getName());
	}

	/**
	 * Perform audit logging after the method has been called.
	 * <p>
	 * This interceptor is equivalent to an Around aspect of the method that
	 * has the Cache annotation(s) - e.g. @CachePut.
	 * <p>
	 * This interceptor does not distinguish cache operations, so all executions
	 * of the application caching method will create audit records.
	 * If this behavior is undesirable, it will be necessary to override enough
	 * of the inherited code to have control of the {@link #doGet(Cache, Object)} method.
	 */
	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		Class<?> underAudit = invocation.getThis().getClass();
		AuditEventData auditEventData = new AuditEventData(AuditEvents.CACHED_SERVICE_RESPONSE, ACTIVITY, underAudit.getName());

		Object response = null;

		try {
			response = super.invoke(invocation);
			if (response == null) {
				// no response
				response = new Object();
			}

			if (LOGGER.isDebugEnabled()) {
				String prefix = this.getClass().getSimpleName() + ".invoke(..) :: ";
				LOGGER.debug(prefix + "Invocation class: " + invocation.getClass().toGenericString());
				LOGGER.debug(prefix + "Invoked from: " + invocation.getThis().getClass().getName());
				LOGGER.debug(prefix + "Invoking method: " + invocation.getMethod().toGenericString());
				LOGGER.debug(prefix + "  having annotations: " + Arrays.toString(invocation.getStaticPart().getAnnotations()));
				LOGGER.debug(prefix + "Returning: " + ReflectionToStringBuilder.toString(response, null, false, false, Object.class));
			}

			ResponseAuditData auditData = new ResponseAuditData();
			auditData.setResponse(response);
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, auditData, ResponseAuditData.class,
					MessageSeverity.INFO, null);
			LOGGER.debug(ADVICE_NAME + " audit logging handed off to async.");

		} catch (Throwable throwable) { // NOSONAR intentionally catching throwable
			this.handleInternalException(ADVICE_NAME, ACTIVITY, auditEventData, throwable);
			throw throwable;
		} finally {
			LOGGER.debug(ADVICE_NAME + " finished.");
		}

		return response;
	}

	/**
	 * Standard handling of exceptions that are thrown from within the advice
	 * (not exceptions thrown by application code).
	 *
	 * @param adviceName the name of the advice method in which the exception was thrown
	 * @param attemptingTo the attempted task that threw the exception
	 * @param auditEventData the audit event data object
	 * @param throwable the exception that was thrown
	 */
	protected void handleInternalException(final String adviceName, final String attemptingTo,
			final AuditEventData auditEventData, final Throwable throwable) {

		try {
			MessageKeys key = MessageKeys.BIP_AUDIT_CACHE_ERROR_UNEXPECTED;
			LOGGER.error(key.getMessage(adviceName, attemptingTo), throwable);
			final BipRuntimeException bipRuntimeException = new BipRuntimeException(
					key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable);
			writeAuditError(adviceName, bipRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			handleAnyRethrownExceptions(adviceName, e);
		}
	}

	/**
	 * If - after attempting to audit an internal error - another exception is thrown,
	 * then put the whole mess on a response entity Message and return it.
	 *
	 * @param adviceName
	 * @param e
	 * @return ResponseEntity
	 */
	private void handleAnyRethrownExceptions(
			final String adviceName, final Throwable e) {

		String msg = adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.";
		LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param bipRuntimeException
	 * @param auditEventData
	 * @return
	 */
	private void writeAuditError(final String adviceName, final BipRuntimeException bipRuntimeException,
			final AuditEventData auditEventData) {

		LOGGER.error(adviceName + " encountered uncaught exception.", bipRuntimeException);

		AuditLogger.error(auditEventData,
				"Error ServiceMessage: " + bipRuntimeException.getMessage(),
				bipRuntimeException);
	}
}
