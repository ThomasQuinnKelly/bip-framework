package gov.va.bip.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * Custom extension of RuntimeException so that we can raise this for exceptions we have no intention
 * of handling and need to raise but for some reason cannot raise
 * java's RuntimeException or allow the original exception to simply propagate.
 *
 * @author akulkarni
 */
public class BipFeignRuntimeException extends BipRuntimeException {
	private static final long serialVersionUID = 2598842813684506356L;

	/**
	 * Constructs a new RuntimeException with the specified detail key, message, severity, and status. The cause is not initialized.
	 *
	 * @see BipRuntimeException#BipRuntimeException(String, String, MessageSeverity, HttpStatus)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipFeignRuntimeException(final MessageKey key, final MessageSeverity severity, final HttpStatus status, final String... params) {
		super(key, severity, status, params);
	}

	/**
	 * Constructs a new RuntimeException with the specified detail key, message, severity, and status. The cause is not initialized.
	 *
	 * @see BipRuntimeException#BipRuntimeException(String, String, MessageSeverity, HttpStatus, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 * @param cause - the throwable that caused this throwable
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	public BipFeignRuntimeException(final MessageKey key, final MessageSeverity severity, final HttpStatus status, final Throwable cause, final String... params) {
		super(key, severity, status, cause, params);
	}
}
