package gov.va.ocp.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * The root OCP class for validation or defense <b>runtime</b> exceptions.
 * <p>
 * To support the consistency in validation errors, all OCP validation
 * exceptions should be this class, or a sub-class of this class.
 *
 * @see OcpRuntimeException
 *
 * @author aburkholder
 */
public class OcpValidationRuntimeException extends OcpRuntimeException {
	private static final long serialVersionUID = -3876995562701933677L;

	/**
	 * Constructs a new <b>runtime</b> Exception indicating a validation or defense error.
	 *
	 * @see OcpRuntimeException#OcpRuntimeException(String, String, MessageSeverity, HttpStatus)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 */
	public OcpValidationRuntimeException(String key, String message, MessageSeverity severity, HttpStatus status) {
		super(key, message, severity, status);
	}

	/**
	 * Constructs a new <b>runtime</b> Exception indicating a {@link gov.va.ocp.framework.validationValidator}
	 * or defense error.
	 *
	 * @see OcpRuntimeException#OcpRuntimeException(String, String, MessageSeverity, HttpStatus, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param cause - the throwable that caused this throwable
	 */
	public OcpValidationRuntimeException(String key, String message, MessageSeverity severity, HttpStatus status, Throwable cause) {
		super(key, message, severity, status, cause);
	}
}
