package gov.va.bip.framework.shared.sanitize;

/**
 * Thrown to indicate that an unrecoverable problem was encountered in a {@link Sanitizer} method.
 *
 * @author aburkholder
 */
public class SanitizerException extends RuntimeException {
	private static final long serialVersionUID = 8962232363339180639L;

	/**
	 * Constructs an {@code SanitizerException} with no
	 * detail message.
	 */
	public SanitizerException() {
		super();
	}

	/**
	 * Constructs a {@code SanitizerException} with the
	 * specified detail message.
	 *
	 * @param s the detail message.
	 */
	public SanitizerException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code SanitizerException} with the specified cause and a detail
	 * message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example, {@link
	 * java.security.PrivilegedActionException}).
	 *
	 * @param cause the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public SanitizerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code SanitizerException} with the specified detail message and
	 * cause.
	 *
	 * <p>Note that the detail message associated with {@code cause} is
	 * <i>not</i> automatically incorporated in this exception's detail
	 * message.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *            by the {@link Throwable#getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A {@code null} value
	 *            is permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public SanitizerException(String message, Throwable cause) {
		super(message, cause);
	}
}
