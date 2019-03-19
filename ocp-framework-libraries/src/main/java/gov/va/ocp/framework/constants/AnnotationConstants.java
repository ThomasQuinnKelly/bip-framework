package gov.va.ocp.framework.constants;

/**
 * Collection of constants for use with annotations. This will help avoid duplicate literals
 * in the code.
 *
 * @author jluck
 * @version 1.0
 *
 */
public final class AnnotationConstants {
	/*
	 * Constants for use with java.lang.SuppressWarnings to
	 * ignore unchecked class casting
	 * 
	 * @see java.lang.SuppressWarnings
	 */
	/** Constant to suppress unchecked */
	public static final String UNCHECKED = "unchecked";
	/** Constant to suppress Interceptor Exception */
	public static final String INTERCEPTOR_EXCEPTION = "Interceptor Exception";

	/**
	 * This is a class for statics. Do not instantiate it.
	 */
	private AnnotationConstants() {
		throw new IllegalStateException("Utility class");
	}
}
