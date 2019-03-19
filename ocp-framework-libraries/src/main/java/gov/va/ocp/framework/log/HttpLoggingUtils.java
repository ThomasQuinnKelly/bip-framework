package gov.va.ocp.framework.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.transport.TransportOutputStream;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * Utilities class with some static methods to log a given web service http message
 */
public class HttpLoggingUtils {
	public static final String UNABLE_TO_LOG_HTTP_MESSAGE_TEXT = "Unable to log HTTP message.";

	public static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(HttpLoggingUtils.class);

	private static final String NEW_LINE = System.getProperty("line.separator");

	static AuditLogSerializer asyncLogging = new AuditLogSerializer();

	private HttpLoggingUtils() {
	}

	/**
	 * Log an INFO SOAP message asynchronously.
	 * 
	 * @param title brief title for the message
	 * @param webServiceMessage the message
	 * @param auditActivity the activity to which the message is logged
	 * @param auditClassName the class from with the message is logged
	 * @param auditEvent the event being logged
	 */
	public static void logMessage(final String title, final WebServiceMessage webServiceMessage, final String auditActivity,
			final String auditClassName, AuditEvents auditEvent) {
		try {
			ByteArrayTransportOutputStream byteArrayTransportOutputStream = new ByteArrayTransportOutputStream();
			webServiceMessage.writeTo(byteArrayTransportOutputStream);

			String httpMessage = new String(byteArrayTransportOutputStream.toByteArray(), "ISO-8859-1");
			AuditEventData auditEventData = new AuditEventData(auditEvent, auditActivity, auditClassName);
			asyncLogging.asyncLogMessageAspectAuditData(auditEventData, NEW_LINE + title + " : " + NEW_LINE + httpMessage + NEW_LINE,
					MessageSeverity.INFO, null);
		} catch (Exception e) {
			LOGGER.error(UNABLE_TO_LOG_HTTP_MESSAGE_TEXT, e);
		}
	}

	public static class ByteArrayTransportOutputStream extends TransportOutputStream {

		private ByteArrayOutputStream byteArrayOutputStream;

		@Override
		public void addHeader(final String name, final String value) throws IOException {
			createOutputStream();
			String header = name + ": " + value + NEW_LINE;
			byteArrayOutputStream.write(header.getBytes());
		}

		@Override
		protected OutputStream createOutputStream() throws IOException {
			if (byteArrayOutputStream == null) {
				byteArrayOutputStream = new ByteArrayOutputStream();
			}
			return byteArrayOutputStream;
		}

		public byte[] toByteArray() {
			return byteArrayOutputStream.toByteArray();
		}
	}
}
