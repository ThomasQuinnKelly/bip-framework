package gov.va.bip.framework.test.exception;

/**
 * The Class BipTestLibRuntimeException.
 */
public class BipTestLibRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new bip test lib runtime exception.
	 *
	 * @param message the message
	 * @param t the throwable
	 */
	public BipTestLibRuntimeException(final String message, final Throwable t) {
		super(message, t);
	}

	/**
	 * Instantiates a new bip test lib runtime exception.
	 *
	 * @param message the message
	 */
	public BipTestLibRuntimeException(final String message) {
		super(message);
	}

}
