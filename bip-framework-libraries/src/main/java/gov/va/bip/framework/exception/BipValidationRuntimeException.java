package gov.va.bip.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * The root BIP class for validation or defense <b>runtime</b> exceptions.
 * <p>
 * To support the consistency in validation errors, all BIP validation
 * exceptions should be this class, or a sub-class of this class.
 *
 * @see BipRuntimeException
 *
 * @author aburkholder
 */
public class BipValidationRuntimeException extends BipRuntimeException {
	private static final long serialVersionUID = -3876995562701933677L;

	/**
	 * Constructs a new <b>runtime</b> Exception indicating a validation or defense error.
	 *
	 * @see BipRuntimeException#BipRuntimeException(String, String, MessageSeverity, HttpStatus)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipValidationRuntimeException(MessageKey key, MessageSeverity severity, HttpStatus status, Object... params) {
		super(key, severity, status, params);
	}

	/**
	 * Constructs a new <b>runtime</b> Exception indicating a {@link gov.va.bip.framework.validation.Validator}
	 * or defense error.
	 *
	 * @see BipRuntimeException#BipRuntimeException(String, String, MessageSeverity, HttpStatus, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param cause - the throwable that caused this throwable
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipValidationRuntimeException(MessageKey key, MessageSeverity severity, HttpStatus status,
			Throwable cause, Object... params) {
		super(key, severity, status, cause, params);
	}
}
