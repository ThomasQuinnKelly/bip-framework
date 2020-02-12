package gov.va.bip.framework.log;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.RequestResponseLogSerializer;
import gov.va.bip.framework.messages.MessageSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.transport.TransportOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utilities class with some static methods to log a given web service http message
 */
public class HttpLoggingUtils {
	public static final String UNABLE_TO_LOG_HTTP_MESSAGE_TEXT = "Unable to log HTTP message.";

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpLoggingUtils.class);

	private static final String NEW_LINE = System.getProperty("line.separator");

	static RequestResponseLogSerializer asyncLogging = new RequestResponseLogSerializer();

	private HttpLoggingUtils() {
	}

	public static void logMessage(final String title, final WebServiceMessage webServiceMessage, final String auditActivity,
			final String auditClassName, AuditEvents auditEvent) {
		try {
			ByteArrayTransportOutputStream byteArrayTransportOutputStream = new ByteArrayTransportOutputStream();
			webServiceMessage.writeTo(byteArrayTransportOutputStream);

			String httpMessage = new String(byteArrayTransportOutputStream.toByteArray(), StandardCharsets.UTF_8);
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
