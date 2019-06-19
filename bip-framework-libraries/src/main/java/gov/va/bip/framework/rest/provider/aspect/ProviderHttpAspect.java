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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.exception.BipExceptionExtender;
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
 * @see gov.va.bip.framework.rest.provider.aspect.BaseHttpProviderPointcuts
 */
@Aspect
@Order(-9998)
public class ProviderHttpAspect extends BaseHttpProviderPointcuts {

	private static final String FINISHED_STRING = " finished.";
	private static final String JOINPOINT_STRING = " joinpoint: ";

	/**
	 * Developers note: for thread safety, only static constants
	 * or spring proxies can be put at class level.
	 */

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
			super.auditServletRequest().writeHttpRequestAuditLog(requestArgs, auditEventData);

		} catch (final Throwable throwable) { // NOSONAR intentionally catching throwable
			LOGGER.error(this.getClass().getSimpleName() + " " + BEFORE_ADVICE + " while attempting " + ATTEMPTING_WRITE_REQUEST
					+ ": " + throwable.getClass().getSimpleName() + " " + throwable.getMessage(),
					throwable);
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

			super.auditServletResponse().writeHttpResponseAuditLog(providerResponse, auditEventData, MessageSeverity.INFO, null);

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
			ProviderResponse response = new ProviderResponse();
			if (BipExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
				BipExceptionExtender bipee = (BipExceptionExtender) throwable;
				response.addMessage(MessageSeverity.ERROR, bipee.getExceptionData().getKey(), throwable.getMessage(),
						bipee.getExceptionData().getStatus());
			} else {
				MessageKeys key = MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION;
				response.addMessage(MessageSeverity.ERROR, key.getKey(),
						key.getMessage(throwable.getClass().getSimpleName(), throwable.getMessage()), HttpStatus.BAD_REQUEST);
			}

			auditServletResponse().writeHttpResponseAuditLog(response, auditEventData, MessageSeverity.ERROR, throwable);

		} catch (Throwable t) { // NOSONAR intentionally catching throwable
			providerResponse = handleInternalException(AFTER_THROWING_ADVICE, ATTEMPTING_WRITE_RESPONSE, auditEventData, t);
		} finally {
			LOGGER.debug(AFTER_THROWING_ADVICE + FINISHED_STRING);
		}
		return providerResponse;
	}

}
