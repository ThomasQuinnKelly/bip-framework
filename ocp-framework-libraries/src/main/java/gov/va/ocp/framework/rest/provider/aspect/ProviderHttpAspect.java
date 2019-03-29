package gov.va.ocp.framework.rest.provider.aspect;

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

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;

/**
 * This aspect performs Audit logging before and after the endpoint operation is executed.
 * Additionally, any exceptions thrown back to the endpoint operation will be intercepted
 * and converted to appropriate JSON object with a FATAL message.
 *
 * @author akulkarni
 * @see gov.va.ocp.framework.rest.provider.aspect.BaseHttpProviderAspect
 */
@Aspect
@Order(-9998)
public class ProviderHttpAspect extends BaseHttpProviderAspect {

	/** The Constant LOGGER. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(ProviderHttpAspect.class);

	/**
	 * Perform audit logging on the request, before the operation is executed.
	 *
	 * @param joinPoint
	 */
	@Before("!auditableAnnotation() && publicServiceResponseRestMethod()")
	public void beforeAuditAdvice(final JoinPoint joinPoint) {
		LOGGER.debug("beforeAuditAdvice joinpoint: " + joinPoint.toLongString());

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
			writeRequestInfoAudit(requestArgs, auditEventData);

		} catch (final Throwable throwable) { // NOSONAR intentionally catching throwable
			handleInternalException("beforeAuditAdvice", "writeRequestInfoAudit", auditEventData, throwable);
		} finally {
			LOGGER.debug("beforeAuditAdvice finished.");
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
	public void afterreturningAuditAdvice(JoinPoint joinPoint, ProviderResponse responseToConsumer) {
		LOGGER.debug("afterreturningAuditAdvice joinpoint: " + joinPoint.toLongString());
		LOGGER.debug(
				"afterreturningAuditAdvice responseToConsumer: "
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
			handleInternalException("afterreturningAuditAdvice", "writeResponseAudit", auditEventData, throwable);
		} finally {
			LOGGER.debug("afterreturningAuditAdvice finished.");
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
	public ResponseEntity<ProviderResponse> afterThrowingAdvice(JoinPoint joinPoint, Throwable throwable) {
		LOGGER.debug("afterThrowingAdvice joinpoint: " + joinPoint.toLongString());
		LOGGER.debug("afterThrowingAdvice throwable: {}" + throwable);

		AuditEventData auditEventData = null;
		ResponseEntity<ProviderResponse> providerResponse = null;

		try {
			if (throwable == null) {
				// null throwable almost certain not to happen, but check nonetheless
				throwable = new Throwable("Unknown (null) exception.");
			}

			OcpRuntimeException ocpException = null;
			if (!OcpExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
				ocpException = new OcpRuntimeException("", "Converting " + throwable.getClass().getSimpleName()
						+ " to OcpRuntimeException.  Original message: " + throwable.getMessage(),
						MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
						throwable.getCause());
			} else {
				ocpException = (OcpRuntimeException) throwable;
			}

			providerResponse = writeAuditError("afterThrowingAdvice", ocpException, auditEventData);

		} catch (Throwable t) { // NOSONAR intentionally catching throwable
			providerResponse = handleInternalException("afterThrowingAdvice", "writeResponseAudit", auditEventData, t);
		} finally {
			LOGGER.debug("afterThrowingAdvice finished.");
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
	private ResponseEntity<ProviderResponse> handleInternalException(String adviceName, String attemptingTo,
			AuditEventData auditEventData, Throwable throwable) {
		ResponseEntity<ProviderResponse> entity = null;
		try {
			final OcpRuntimeException ocpRuntimeException =
					new OcpRuntimeException("", adviceName + " - Exception occured while attempting to " + attemptingTo + ".",
							MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
							throwable);
			entity = writeAuditError(adviceName, ocpRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			entity = handleAnyRethrownExceptions(adviceName, e);
		}
		return entity;
	}

	private ResponseEntity<ProviderResponse> handleAnyRethrownExceptions(String adviceName, Throwable e) {
		ResponseEntity<ProviderResponse> entity;
		LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.", e);
		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.",
				HttpStatus.INTERNAL_SERVER_ERROR);
		entity = new ResponseEntity<ProviderResponse>(body, HttpStatus.INTERNAL_SERVER_ERROR);
		return entity;
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param ocpRuntimeException
	 * @param auditEventData
	 * @return
	 */
	private ResponseEntity<ProviderResponse> writeAuditError(final String adviceName, final OcpRuntimeException ocpRuntimeException,
			final AuditEventData auditEventData) {
		LOGGER.error(adviceName + " encountered uncaught exception.", ocpRuntimeException);
		final ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ocpRuntimeException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		final StringBuilder sb = new StringBuilder();
		sb.append("Error ServiceMessage: ").append(ocpRuntimeException);
		AuditLogger.error(auditEventData, sb.toString(), ocpRuntimeException);
		return new ResponseEntity<>(providerResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
