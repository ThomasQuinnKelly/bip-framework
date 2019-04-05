package gov.va.bip.framework.service;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * Root hierarchy of exceptions which indicates there was an
 * exception/error in the Service (domain) layers.
 *
 * @see gov.va.bip.framework.exception.BipRuntimeException
 *
 * @author aburkholder
 */
public class BipServiceException extends BipRuntimeException {
	private static final long serialVersionUID = -6590361959617339905L;

	/**
	 * Constructs a new BipServiceException with the specified detail key, message, severity, and status.
	 * The cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @see RuntimeException#RuntimeException(String)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipServiceException(MessageKey key, MessageSeverity severity, HttpStatus status, Object... params) {
		super(key, severity, status, params);
	}

	/**
	 * Constructs a new BipServiceException with the specified detail key, message, severity, status, and cause.
	 *
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param cause - the throwable that caused this throwable
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipServiceException(MessageKey key, MessageSeverity severity, HttpStatus status, Throwable cause, Object... params) {
		super(key, severity, status, cause, params);
	}
}
