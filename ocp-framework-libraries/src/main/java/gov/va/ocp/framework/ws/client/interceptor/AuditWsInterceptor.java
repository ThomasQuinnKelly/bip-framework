package gov.va.ocp.framework.ws.client.interceptor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.exception.OcpPartnerRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.validation.Defense;
import gov.va.ocp.framework.ws.client.interceptor.transport.ByteArrayTransportOutputStream;

/**
 * This interceptor performs Audit logging of the request and response XML from the {@link WebserviceTemplate}.
 * Also, any SOAP Faults on the WebServiceTemplate operation will be audited.
 *
 * @author aburkholder
 */
public class AuditWsInterceptor implements ClientInterceptor {
	/** Class logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(AuditWsInterceptor.class);

	/** Asynchronous audit logger */
	private static final AuditLogSerializer asyncLogging = new AuditLogSerializer();

	/** Ensure logging only occurs once per instantiation */
	private boolean alreadyLogged = false;

	/** The text of the title of the audit log */
	private AuditWsInterceptorConfig config;

	/**
	 * Instantiate the interceptor to use the given configuration.
	 *
	 * @param config the config
	 */
	public AuditWsInterceptor(AuditWsInterceptorConfig config) {
		Defense.notNull(config);
		LOGGER.debug("Instantiating " + this.getClass().getSimpleName() + " with config: " + config.name());
		this.config = config;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) {
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) {
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) {
		LOGGER.debug("Executing handleFault(..) with config " + config.name());
		doAudit(config.faultMetadata(), messageContext.getResponse());
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Exception ex) {
		if (!alreadyLogged) {
			LOGGER.debug("Executing afterCompletion(..) with config " + config.name());
			// log request
			doAudit(config.requestMetadata(), messageContext.getRequest());

			LOGGER.debug("Partner call returned response: " + messageContext.getResponse());
			// log response, even if it is null
			doAudit(config.responseMetadata(), messageContext.getResponse());

			// remember that this interceptor has already done its job
			alreadyLogged = true;
		}
	}

	/**
	 * Asynchronously writes the audit information to the audit log.
	 *
	 * @param event the AuditEvent
	 * @param activityName the activity name
	 * @param title the title prepended to the message
	 * @param webServiceMessage the WebServiceMessage that contains the SOAP XML
	 * @throws IOException some problem reading the WebServiceMessage
	 */
	private void doAudit(AuditWsInterceptorConfig.AuditWsMetadata metadata,
			WebServiceMessage webServiceMessage) {
		LOGGER.debug("Writing audit log with metadata: " + metadata.getClass().getName());
		try {
			asyncLogging.asyncLogMessageAspectAuditData(metadata.eventData(),
					metadata.messagePrefix() + getXml(webServiceMessage),
					MessageSeverity.INFO, null);
		} catch (Exception e) {
			handleInternalError(metadata.event(), metadata.activity(), e);
		}
	}

	/**
	 * Gets the XML (SOAP) representation of the {@link WebServiceMessage}.
	 *
	 * @param webServiceMessage
	 * @return String - the SOAP XML
	 * @throws IOException
	 */
	private String getXml(WebServiceMessage webServiceMessage) throws IOException {
		if (webServiceMessage == null) {
			return null;
		}

		ByteArrayTransportOutputStream byteArrayTransportOutputStream = new ByteArrayTransportOutputStream();
		webServiceMessage.writeTo(byteArrayTransportOutputStream);

		return new String(byteArrayTransportOutputStream.toByteArray(), "ISO-8859-1");
	}

	/**
	 * Handles any exception thrown internally.
	 * <ul>
	 * <li>The error is written to the audit log
	 * <li>The following exception types are re-thrown untouched: OcpPartnerRuntimeException, WebServiceClientException.
	 * <li>All other exception types are converted to OcpPartnerRuntimeException which is then thrown.
	 * </ul>
	 *
	 * @param event
	 * @param activity
	 * @param e
	 * @throws RuntimeException
	 */
	protected void handleInternalError(final AuditEvents event, final String activity, final Exception e) {
		RuntimeException rethrowMe = null;
		String adviceName = this.getClass().getSimpleName() + ".afterCompletion";
		this.writeAuditError(adviceName, e, new AuditEventData(event, activity, config.auditedName()));
		if (OcpPartnerRuntimeException.class.isAssignableFrom(e.getClass())) {
			rethrowMe = (OcpPartnerRuntimeException) e;
		} else if (WebServiceClientException.class.isAssignableFrom(e.getClass())) {
			rethrowMe = (WebServiceClientException) e;
		} else {
			rethrowMe =
					new OcpPartnerRuntimeException("", "Unexpected exception thrown by WebServiceTemplate. Please investigate.",
							MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		throw rethrowMe;
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param adviceName the advice name
	 * @param e the exception
	 * @param auditEventData the audit event data
	 */
	protected void writeAuditError(final String adviceName, final Exception e, final AuditEventData auditEventData) {
		LOGGER.error(adviceName + " encountered uncaught exception.", e);
		AuditLogger.error(auditEventData,
				adviceName + " encountered uncaught exception: " + e.getLocalizedMessage(), e);
	}
}
