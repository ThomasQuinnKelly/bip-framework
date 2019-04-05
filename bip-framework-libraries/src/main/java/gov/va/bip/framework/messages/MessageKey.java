package gov.va.bip.framework.messages;

/**
 * The interface for BIP message keys and their messages.
 * <p>
 * Implementations of this interface should use the spring MessageSource or similar
 * spring mechanism to retrieve message values from the spring context.
 *
 * @author aburkholder
 */
public interface MessageKey {

	/**
	 * Get the property key for this enumeration.
	 *
	 * @return String - the key
	 */
	public String getKey();

	/**
	 * Get the message for this property, as resolved from the properties file.
	 * The supplied params are values to replace params in the message stored in the properties file,
	 * e.g. the value for {0}.
	 * <p>
	 * If the key for this enumeration is not found in the properties file,
	 * the default message is returned.
	 *
	 * @param params - an array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}",
	 *            "{2,time}" within a message), or null if none.
	 * @return String - the resolved message, or default message
	 */
	public String getMessage(Object... params);
}
