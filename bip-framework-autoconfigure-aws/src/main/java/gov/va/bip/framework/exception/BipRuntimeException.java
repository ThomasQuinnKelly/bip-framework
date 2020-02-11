package gov.va.bip.framework.exception;


import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;
import org.springframework.http.HttpStatus;

public class BipRuntimeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2598842813684506358L;

	/** Server name exception occurred on */
	public static final String SERVER_NAME = System.getProperty("server.name");

	/**
	 * Instantiates a new exception.
	 */
	public BipRuntimeException() {
		super();
	}
	/**
	 * Instantiates a new exception.
	 * @param key
	 * @param severity
	 * @param status
	 * @param params
	 */
	public BipRuntimeException(MessageKey key, MessageSeverity severity, HttpStatus status, String[] params) {
		super();
	}

	/**
	 * Instantiates a new service exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public BipRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new service exception.
	 *
	 * @param message the message
	 */
	public BipRuntimeException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new service exception.
	 *
	 * @param key
	 * @param severity
	 * @param status
	 * @param cause the cause
	 * @param params
	 */
	public BipRuntimeException(MessageKey key, MessageSeverity severity, HttpStatus status, final Throwable cause, String[] params) {
		super(cause);
	}

	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public final String getServerName() {
		return SERVER_NAME;
	}
}
