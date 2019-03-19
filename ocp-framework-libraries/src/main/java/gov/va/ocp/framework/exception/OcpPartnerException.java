package gov.va.ocp.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * The root OCP class for managing <b>checked</b> exceptions to be thrown from
 * <i>Partner Clients</i>. A Partner Client could be a separate project/jar that is
 * included in the micro-service, or a packaged layer in the service project.
 * <p>
 * To support the consistency in partner responses, all OCP partner jars/packages
 * that throw checked exceptions should throw this class, or a sub-class of this class.
 *
 * @see OcpException
 *
 * @author aburkholder
 */
public class OcpPartnerException extends OcpException {
	private static final long serialVersionUID = -1657198082980424519L;

	/**
	 * Constructs a new <b>checked</b> Exception indicating a problem occurred in the external partner
	 * that requires the application to make a decision.
	 * Examples could include scenarios like "requested data not found", "input data malformed",
	 * etc.
	 *
	 * @see OcpException#OcpException(String, String, MessageSeverity, HttpStatus)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 */
	public OcpPartnerException(String key, String message, MessageSeverity severity, HttpStatus status) {
		super(key, message, severity, status);
	}

	/**
	 * Constructs a new <b>checked</b> Exception indicating a problem occurred in the external partner
	 * that requires the application to make a decision.
	 * Examples could include scenarios like "requested data not found", "input data malformed",
	 * etc.
	 *
	 * @see OcpException#OcpException(String, String, MessageSeverity, HttpStatus, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param cause - the throwable that caused this throwable
	 */
	public OcpPartnerException(String key, String message, MessageSeverity severity, HttpStatus status, Throwable cause) {
		super(key, message, severity, status, cause);
	}
}
