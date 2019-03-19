package gov.va.ocp.framework.ws.client.remote;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.RequestAuditData;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.audit.ResponseAuditData;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * Audit log partner request/response data
 *
 * @see org.aopalliance.intercept.MethodInterceptor
 */
@Component
public class AuditAroundRemoteServiceCallInterceptor implements MethodInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditAroundRemoteServiceCallInterceptor.class);

	@Autowired
	AuditLogSerializer asyncLogging;

	/**
	 * Spring-managed interceptor that executes equivalent of an {@code @Around} aspect.
	 * <p>
	 * This interceptor executes, in order, the following tasks:
	 * <ol>
	 * <li>Collect audit data and log to async audit logger
	 * <li>
	 * </ol>
	 *
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public final Object invoke(final MethodInvocation methodInvocation) throws Throwable {

		/* Create audit log for request to partner client */
		String paramtypesToLog = "";
		for (Class<?> c : methodInvocation.getMethod().getParameterTypes()) {
			paramtypesToLog += c.getName() + ", ";
		}
		paramtypesToLog = StringUtils.removeEnd(paramtypesToLog, ", ");
		LOGGER.info("Intercepted: " + methodInvocation.getMethod().getDeclaringClass().getName()
				+ "." + methodInvocation.getMethod().getName()
				+ "(" + paramtypesToLog + ")");

		Object[] args = methodInvocation.getArguments();
		LOGGER.info("Number of Arguments : " + args.length + "; values: " + ReflectionToStringBuilder.reflectionToString(args));

		final RequestAuditData requestAuditData = new RequestAuditData();
		requestAuditData.setRequest(Arrays.asList(args));

		LOGGER.debug("Invoking asyncLogRequestResponseAspectAuditData");
		AuditEventData auditEventData = new AuditEventData(AuditEvents.PARTNER_SOAP_REQUEST, methodInvocation.getMethod().getName(),
				methodInvocation.getMethod().getDeclaringClass().getSimpleName());
		asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, requestAuditData, RequestAuditData.class,
				MessageSeverity.INFO, null);

		/* Call the partner client */
		Object retVal = "";
		try {
			retVal = methodInvocation.proceed();
		} catch (Exception e) {
			/* Catch; Audit Log and Rethrow exception */
			String errMsg = e.getMessage() != null ? e.getMessage() : " null.";
			LOGGER.error("Partner error: methodName {} " + methodInvocation.getMethod() + "; method args {} "
					+ ReflectionToStringBuilder.toString(methodInvocation.getArguments()) + ".\n Message: " + errMsg, e);
			asyncLogging.asyncLogMessageAspectAuditData(auditEventData, errMsg, MessageSeverity.ERROR, e);
			throw e;
		}

		/* Create audit log for response from partner client */
		final ResponseAuditData resonseAuditData = new ResponseAuditData();
		resonseAuditData.setResponse(retVal);

		LOGGER.debug("Invoking asyncLogRequestResponseAspectAuditData");
		auditEventData = new AuditEventData(AuditEvents.PARTNER_SOAP_RESPONSE, methodInvocation.getMethod().getName(),
				methodInvocation.getMethod().getDeclaringClass().getSimpleName());
		asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, resonseAuditData, ResponseAuditData.class,
				MessageSeverity.INFO, null);
		return retVal;
	}

}
