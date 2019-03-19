package gov.va.ocp.framework.rest.provider.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.Auditable;
import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

@Aspect
public class LogAnnotatedMethodRequestResponseAspect extends BaseHttpProviderAspect {
	/** The Constant LOGGER. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(LogAnnotatedMethodRequestResponseAspect.class);

	public LogAnnotatedMethodRequestResponseAspect() {
		super();
	}

	/**
	 * Advice to log methods that are annotated with @Auditable. Separately logs the call to the method and its arguments, and the
	 * response from the method.
	 *
	 * @param joinPoint
	 *            the join point
	 * @return the object
	 */
	@Around("auditableExecution()")
	public Object logAnnotatedMethodRequestResponse(final ProceedingJoinPoint joinPoint) throws Throwable {
		Object response = null;
		List<Object> request = null;

		try {
			if (joinPoint.getArgs().length > 0) {
				request = Arrays.asList(joinPoint.getArgs());
			}

			final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			LOGGER.debug("Method: {}", method);
			final Auditable auditableAnnotation = method.getAnnotation(Auditable.class);
			LOGGER.debug("Auditable Annotation: {}", auditableAnnotation);
			AuditEventData auditEventData;
			if (auditableAnnotation != null) {
				auditEventData =
						new AuditEventData(auditableAnnotation.event(), auditableAnnotation.activity(),
								auditableAnnotation.auditClass());
				LOGGER.debug("AuditEventData: {}", auditEventData.toString());

				writeRequestInfoAudit(request, auditEventData);
			}

			response = joinPoint.proceed();

			LOGGER.debug("Response: {}", response);

			if (auditableAnnotation != null) {
				auditEventData = new AuditEventData(auditableAnnotation.event(), auditableAnnotation.activity(),
						auditableAnnotation.auditClass());
				writeResponseAudit(response, auditEventData, MessageSeverity.INFO, null);
			}
		} catch (Throwable e) {
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					"Error while executing logAnnotatedMethodRequestResponse around auditableExecution", e);
		}
		return response;
	}

}
