package gov.va.ocp.framework.exception;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * The root OCP interface for managing throwables, runtime exceptions, and checked exceptions.
 * <p>
 * To support the requirements of consumer responses, all OCP Exception classes should
 * implement this interface.
 * <p>
 * Implied private properties are:
 * <ul>
 * <li>String key: the consumer-facing key that can uniquely identify the nature of the exception
 * <li>MessageSeverity severity: the severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or
 * INFO/DEBUG/TRACE
 * <li>HttpStatus status: the HTTP Status code that applies best to the encountered problem, see
 * <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
 * </ul>
 * Implementers of this interface should provide two constructors in the form of:
 * <table style="border-collapse:collapse;">
 * <tr>
 * <td></td>
 * <td>{@code public OcpYourException(String key, String message, MessageSeverity severity, HttpStatus status)}</td>
 * </tr>
 * <tr>
 * <td>and</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>{@code public OcpYourException(String key, String message, MessageSeverity severity, HttpStatus status, Throwable cause)}</td>
 * </tr>
 * </table>
 *
 * @author aburkholder
 */
public interface OcpExceptionExtender {

	/**
	 * The consumer-facing key that can uniquely identify the nature of the exception
	 *
	 * @return the key
	 */
	public String getKey();

	/**
	 * The HTTP Status code that applies best to the encountered problem, see
	 * <a href="https://tools.ietf.org/html/rfc7231">https://tools.ietf.org/html/rfc7231</a>
	 *
	 * @return the status
	 */
	public HttpStatus getStatus();

	/**
	 * The severity of the event: FATAL (500 series), ERROR (400 series), WARN (200 series), or INFO/DEBUG/TRACE
	 *
	 * @return the severity
	 */
	public MessageSeverity getSeverity();

	/**
	 * The server name that the exception occurred on.
	 * <p>
	 * Implementations may simply return the SERVER_NAME constant.
	 *
	 * @return String the server name
	 */
	public String getServerName();
}
