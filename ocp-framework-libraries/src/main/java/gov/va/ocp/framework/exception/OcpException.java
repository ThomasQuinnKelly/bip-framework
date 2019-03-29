package gov.va.ocp.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * The root OCP class for managing <b>checked</b> exceptions.
 * <p>
 * To support the requirements of consumer responses, all OCP checked Exception classes
 * that will be handled internally by the service should extend this class.
 *
 * @see OcpExceptionExtender
 * @see Exception
 *
 * @author aburkholder
 */
public class OcpException extends Exception implements OcpExceptionExtender {
	private static final long serialVersionUID = 4717771104509731434L;

	/** The consumer facing identity key */
	private final String key;
	/** The severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE */
	private final MessageSeverity severity;
	/** The best-fit HTTP Status, see <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a> */
	private final HttpStatus status;

	/**
	 * Constructs a new <b>checked</b> Exception with the specified detail key, message, severity, and status.
	 * The cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @see Exception#Exception(String)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 */
	public OcpException(final String key, final String message, final MessageSeverity severity, final HttpStatus status) {
		super(message);
		this.key = key;
		this.severity = severity;
		this.status = status;
	}

	/**
	 * Constructs a new <b>checked</b> Exception with the specified detail key, message, severity, status, and cause.
	 *
	 * @see Exception#Exception(String, Throwable)
	 *
	 * @param key - the consumer-facing key that can uniquely identify the nature of the exception
	 * @param message - the detail message
	 * @param severity - the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 * @param status - the HTTP Status code that applies best to the encountered problem, see
	 *            <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 * @param cause - the throwable that caused this throwable
	 */
	public OcpException(final String key, final String message, final MessageSeverity severity, final HttpStatus status,
			final Throwable cause) { //NOSONAR
		super(message, cause);       //NOSONAR
		this.key = key;              //NOSONAR
		this.severity = severity;    //NOSONAR
		this.status = status;        //NOSONAR
	}

	@Override                        //NOSONAR
	public String getKey() {         //NOSONAR
		return key;                  //NOSONAR
	}                                //NOSONAR

	@Override                        //NOSONAR
	public HttpStatus getStatus() {  //NOSONAR
		return status;               //NOSONAR
	}                                //NOSONAR

	@Override                        //NOSONAR
	public MessageSeverity getSeverity() {     //NOSONAR
		return severity;             //NOSONAR
	}                                //NOSONAR

	@Override                        //NOSONAR
	public String getServerName() {  //NOSONAR
		return OcpExceptionConstants.SERVER_NAME;          //NOSONAR
	}                                //NOSONAR
}
