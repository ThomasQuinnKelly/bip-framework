package gov.va.bip.framework.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.annotation.Auditable;
import gov.va.bip.framework.audit.model.RequestAuditData;
import gov.va.bip.framework.audit.model.ResponseAuditData;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.aspect.BaseHttpProviderPointcuts;

/**
 * Before and After audit logging for the {@link Auditable} annotation.
 * <p>
 * Note that this aspect does NOT process AfterThrowing advice. Because
 * the {@code @Auditable} annotation could be applied to any method,
 * it has been decided to allow method exceptions to flow through the aspect
 * as-is.
 *
 * @author aburkholder
 */
@Aspect
public class AuditableAnnotationAspect extends BaseHttpProviderPointcuts {
	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(AuditableAnnotationAspect.class);
	
	/** The Constant AUDIT_DEBUG_PREFIX_METHOD. */
	private static final String AUDIT_DEBUG_PREFIX_METHOD = "Audit Annotated Method: {}";
	
	/** The Constant AUDIT_DEBUG_PREFIX_CLASS. */
	private static final String AUDIT_DEBUG_PREFIX_CLASS = "Audit Annotated Class: {}";

	/** The Constant AUDIT_DEBUG_PREFIX_ANNOTATION. */
	private static final String AUDIT_DEBUG_PREFIX_ANNOTATION = "Auditable Annotation: {}";
	
	/** The Constant AUDIT_DEBUG_PREFIX_EVENT. */
	private static final String AUDIT_DEBUG_PREFIX_EVENT = "AuditEventData: {}";
	
	/** The Constant AUDIT_ERROR_PREFIX_EXCEPTION. */
	private static final String AUDIT_ERROR_PREFIX_EXCEPTION = "Could not audit event due to unexpected exception.";

	/**
	 * Instantiate the aspect.
	 */
	public AuditableAnnotationAspect() {
		super();
	}

	/**
	 * Advice for auditing before the call to a method annotated with {@link Auditable}.
	 * <p>
	 * Note that this aspect does NOT process AfterThrowing advice. Because
	 * the {@code @Auditable} annotation could be applied to any method,
	 * it has been decided to allow method exceptions to flow through the aspect
	 * as-is.
	 *
	 * @param joinPoint
	 */
	@Before("auditableExecution()")
	public void auditAnnotationBefore(final JoinPoint joinPoint) {
		List<Object> request = null;
		Auditable auditableAnnotation = null;
		AuditEventData auditEventData = null;

		try {
			if (joinPoint.getArgs().length > 0) {
				request = Arrays.asList(joinPoint.getArgs());
			}

			final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			LOGGER.debug(AUDIT_DEBUG_PREFIX_METHOD, method);
			final String className = method.getDeclaringClass().getName();
			LOGGER.debug(AUDIT_DEBUG_PREFIX_CLASS, className);
			auditableAnnotation = method.getAnnotation(Auditable.class);
			LOGGER.debug(AUDIT_DEBUG_PREFIX_ANNOTATION, auditableAnnotation);
			if (auditableAnnotation != null) {
				auditEventData =
						new AuditEventData(auditableAnnotation.event(), auditableAnnotation.activity(),
								StringUtils.isBlank(auditableAnnotation.auditClass()) ? className : auditableAnnotation.auditClass());
				LOGGER.debug(AUDIT_DEBUG_PREFIX_EVENT, auditEventData.toString());

				baseAsyncAudit.writeRequestAuditLog(request, new RequestAuditData(), auditEventData, null, null);
			}
		} catch (Exception e) { // NOSONAR intentionally broad catch
			baseAsyncAudit.handleInternalExceptionAndRethrowApplicationExceptions("auditAnnotationBefore",
					"AuditingUsingAuditableAnnotation", auditEventData,
					MessageKeys.BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT, e);
		}

	}

	/**
	 * Advice for auditing after the call to a method annotated with {@link Auditable}.
	 * <p>
	 * Note that this aspect does NOT process AfterThrowing advice. Because
	 * the {@code @Auditable} annotation could be applied to any method,
	 * it has been decided to allow method exceptions to flow through the aspect
	 * as-is.
	 *
	 * @param joinPoint
	 * @param response
	 */
	@AfterReturning(pointcut = "auditableExecution()", returning = "response")
	public void auditAnnotationAfterReturning(final JoinPoint joinPoint, final Object response) {
		LOGGER.debug("Response: {}", response);

		Auditable auditableAnnotation = null;
		AuditEventData auditEventData = null;

		try {
			final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			LOGGER.debug(AUDIT_DEBUG_PREFIX_METHOD, method);
			final String className = method.getDeclaringClass().getName();
			LOGGER.debug(AUDIT_DEBUG_PREFIX_CLASS, className);
			auditableAnnotation = method.getAnnotation(Auditable.class);
			LOGGER.debug(AUDIT_DEBUG_PREFIX_ANNOTATION, auditableAnnotation);

			if (auditableAnnotation != null) {
				auditEventData =
						new AuditEventData(auditableAnnotation.event(), auditableAnnotation.activity(),
								StringUtils.isBlank(auditableAnnotation.auditClass()) ? className : auditableAnnotation.auditClass());
				LOGGER.debug(AUDIT_DEBUG_PREFIX_EVENT, auditEventData.toString());

				baseAsyncAudit.writeResponseAuditLog(response, new ResponseAuditData(), auditEventData, null, null);
			}
		} catch (Exception e) { // NOSONAR intentionally broad catch
			LOGGER.error(AUDIT_ERROR_PREFIX_EXCEPTION, e);
			throw new BipRuntimeException(MessageKeys.BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT, MessageSeverity.FATAL,
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Advice for auditing after the call to a method annotated with {@link Auditable}.
	 * <p>
	 * Note that this aspect does NOT process AfterThrowing advice. Because
	 * the {@code @Auditable} annotation could be applied to any method,
	 * it has been decided to allow method exceptions to flow through the aspect
	 * as-is.
	 *
	 * @param joinPoint
	 * @param response
	 * @throws Throwable
	 */
	@AfterThrowing(pointcut = "auditableExecution()", throwing = "throwable")
	public void auditAnnotationAfterThrowing(final JoinPoint joinPoint, final Throwable throwable) throws Throwable { // NOSONAR Have to define generic Throwable exception
		LOGGER.debug("afterThrowing throwable: {}" + throwable);

		Auditable auditableAnnotation = null;
		AuditEventData auditEventData = null;

		try {
			final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			final String className = method.getDeclaringClass().getName();
			LOGGER.debug(AUDIT_DEBUG_PREFIX_CLASS, className);
			auditableAnnotation = method.getAnnotation(Auditable.class);
			LOGGER.debug(AUDIT_DEBUG_PREFIX_ANNOTATION, auditableAnnotation);

			if (auditableAnnotation != null) {
				String auditedClass = StringUtils.isBlank(auditableAnnotation.auditClass())
						? className
						: auditableAnnotation.auditClass();
				auditEventData =
						new AuditEventData(auditableAnnotation.event(), auditableAnnotation.activity(), auditedClass);
				LOGGER.debug(AUDIT_DEBUG_PREFIX_EVENT, auditEventData.toString());

				baseAsyncAudit.writeResponseAuditLog("An exception occurred in " + auditedClass + ".", new ResponseAuditData(),
						auditEventData,
						MessageSeverity.ERROR, throwable);
			}
		} catch (Exception e) { // NOSONAR intentionally broad catch
			LOGGER.error(AUDIT_ERROR_PREFIX_EXCEPTION, e);
			throw new BipRuntimeException(MessageKeys.BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT, MessageSeverity.FATAL,
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}

		throw throwable;
	}
}
