package gov.va.bip.framework.rest.provider.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.AuditLogger;
import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.ProviderResponse;

/**
 * This aspect performs Audit logging before and after the endpoint operation is executed.
 * Additionally, any exceptions thrown back to the endpoint operation will be intercepted
 * and converted to appropriate JSON object with a FATAL message.
 *
 * @author akulkarni
 * @see gov.va.bip.framework.rest.provider.aspect.BaseHttpProviderAspect
 */
@Aspect
@Order(-9998)
public class ProviderHttpAspect extends BaseHttpProviderAspect {

	private static final String FINISHED_STRING = " finished.";
	private static final String JOINPOINT_STRING = " joinpoint: ";
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(ProviderHttpAspect.class);
	/** Identity of the before advice */
	private static final String BEFORE_ADVICE = "beforeAuditAdvice";
	/** Identity of the after advice */
	private static final String AFTER_ADVICE = "afterreturningAuditAdvice";
	/** Identity of the afterThrowing advice */
	private static final String AFTER_THROWING_ADVICE = "afterThrowingAdvice";

	/** Attempting to write the request to the audit logs */
	private static final String ATTEMPTING_WRITE_REQUEST = "writeRequestInfoAudit";
	/** Attempting to write the response to the audit logs */
	private static final String ATTEMPTING_WRITE_RESPONSE = "writeResponseAudit";

	/**
	 * Perform audit logging on the request, before the operation is executed.
	 *
	 * @param joinPoint
	 */
	@Before("!auditableAnnotation() && publicServiceResponseRestMethod()")
	public void beforeAuditAdvice(final JoinPoint joinPoint) {
		LOGGER.debug(BEFORE_ADVICE + JOINPOINT_STRING + joinPoint.toLongString());

		List<Object> requestArgs = null;
		AuditEventData auditEventData = null;

		if (joinPoint.getArgs().length > 0) {
			requestArgs = Arrays.asList(joinPoint.getArgs());
		}

		try {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData = new AuditEventData(AuditEvents.API_REST_REQUEST, method.getName(), method.getDeclaringClass().getName());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request: {}", requestArgs);
				LOGGER.debug("Method: {}", method);
				LOGGER.debug("AuditEventData: {}", auditEventData.toString());
			}
			writeRequestAuditLog(requestArgs, auditEventData);

		} catch (final Throwable throwable) { // NOSONAR intentionally catching throwable
			handleInternalException(BEFORE_ADVICE, ATTEMPTING_WRITE_REQUEST, auditEventData, throwable);
		} finally {
			LOGGER.debug(BEFORE_ADVICE + FINISHED_STRING);
		}
	}

	/**
	 * Perform audit logging on the response, after the operation is executed.
	 *
	 * @param joinPoint
	 * @param responseToConsumer
	 */
	@AfterReturning(pointcut = "!auditableAnnotation() && publicServiceResponseRestMethod()",
			returning = "responseToConsumer")
	public void afterreturningAuditAdvice(final JoinPoint joinPoint, final ProviderResponse responseToConsumer) {
		LOGGER.debug(AFTER_ADVICE + JOINPOINT_STRING + joinPoint.toLongString());
		LOGGER.debug(AFTER_ADVICE + " responseToConsumer: "
				+ ReflectionToStringBuilder.toString(responseToConsumer, null, true, true, ProviderResponse.class));

		AuditEventData auditEventData = null;
		ProviderResponse providerResponse = null;

		try {
			if (responseToConsumer == null) {
				providerResponse = new ProviderResponse();
			} else {
				providerResponse = responseToConsumer;
			}

			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData = new AuditEventData(AuditEvents.API_REST_RESPONSE, method.getName(), method.getDeclaringClass().getName());

			writeResponseAudit(providerResponse, auditEventData, MessageSeverity.INFO, null);

		} catch (Throwable throwable) { // NOSONAR intentionally catching throwable
			handleInternalException(AFTER_ADVICE, ATTEMPTING_WRITE_RESPONSE, auditEventData, throwable);
		} finally {
			LOGGER.debug(AFTER_ADVICE + FINISHED_STRING);
		}
	}

	/**
	 * Perform audit logging after application has thrown an exception.
	 * Any exceptions thrown back to the endpoint operation will be intercepted
	 * by this advice, and converted to appropriate JSON object with FATAL message.
	 *
	 * @param joinPoint the intersection for the pointcut
	 * @param throwable the exception thrown by the application code
	 */
	@AfterThrowing(pointcut = "!auditableAnnotation() && publicServiceResponseRestMethod()",
			throwing = "throwable")
	public ResponseEntity<ProviderResponse> afterThrowingAdvice(final JoinPoint joinPoint, final Throwable throwable) {
		LOGGER.debug(AFTER_THROWING_ADVICE + JOINPOINT_STRING + joinPoint.toLongString());
		LOGGER.debug(AFTER_THROWING_ADVICE + " throwable: {}" + throwable);

		AuditEventData auditEventData = null;
		ResponseEntity<ProviderResponse> providerResponse = null;

		try {
			providerResponse = writeAuditError(AFTER_THROWING_ADVICE,
					// null throwable almost certain not to happen, but check nonetheless
					throwable != null ? throwable : new Throwable("Unknown problem. Thrown exception was null."), auditEventData);

		} catch (Throwable t) { // NOSONAR intentionally catching throwable
			providerResponse = handleInternalException(AFTER_THROWING_ADVICE, ATTEMPTING_WRITE_RESPONSE, auditEventData, t);
		} finally {
			LOGGER.debug(AFTER_THROWING_ADVICE + FINISHED_STRING);
		}
		return providerResponse;
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
	private ResponseEntity<ProviderResponse> handleInternalException(final String adviceName, final String attemptingTo,
			final AuditEventData auditEventData, final Throwable throwable) {
		ResponseEntity<ProviderResponse> entity = null;
		try {
			MessageKeys key = MessageKeys.BIP_AUDIT_ASPECT_ERROR_UNEXPECTED;
			final BipRuntimeException bipRuntimeException = new BipRuntimeException(key,
					MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable, adviceName, attemptingTo);
			entity = writeAuditError(adviceName, bipRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			entity = handleAnyRethrownExceptions(adviceName, throwable, e);
		}
		return entity;
	}

	private ResponseEntity<ProviderResponse> handleAnyRethrownExceptions(final String adviceName, final Throwable originatingThrowable,
			final Throwable e) {
		ResponseEntity<ProviderResponse> entity;
		MessageKeys key = MessageKeys.BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT;
		String msg = key.getMessage(new String[] { adviceName, originatingThrowable.getClass().getSimpleName() });
		LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);

		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				msg, HttpStatus.INTERNAL_SERVER_ERROR);
		entity = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
		return entity;
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param adviceName
	 * @param exception
	 * @param auditEventData
	 * @return
	 */
	private ResponseEntity<ProviderResponse> writeAuditError(final String adviceName, final Throwable exception,
			final AuditEventData auditEventData) {
		String msg = "Error ServiceMessage: " + exception;
		AuditLogger.error(auditEventData, msg, exception);
		LOGGER.error(adviceName + " auditing uncaught exception.", exception);

		final ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		return new ResponseEntity<>(providerResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
