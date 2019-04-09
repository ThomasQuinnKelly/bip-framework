package gov.va.bip.framework.security.jwt.correlation;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * Vaules for user status codes.
 */
public enum UserStatus {

	/** Status of permanent */
	PERMANENT("P"),
	/** Status of active */
	ACTIVE("A"),
	/** Status of temporary */
	TEMPORARY("T");

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(UserStatus.class);

	/** The arbitrary string value of the enumeration */
	private String status;

	/**
	 * Private constructor for enum initialization
	 *
	 * @param status String
	 */
	private UserStatus(final String status) {
		this.status = status;
	}

	/**
	 * The arbitrary String value assigned to the enumeration.
	 *
	 * @return String
	 */
	public String value() {
		return status;
	}

	/**
	 * Get the enumeration for the associated arbitrary String value.
	 * Throws a runtime exception if the string value does not match one of the enumeration values.
	 *
	 * @param stringValue the string value
	 * @return UserStatus - the enumeration
	 * @throws BipRuntimeException if no match of enumeration values
	 */
	public static UserStatus fromValue(final String stringValue) {
		for (UserStatus s : UserStatus.values()) {
			if (s.value().equals(stringValue)) {
				return s;
			}
		}
		MessageKeys key = MessageKeys.BIP_SECURITY_TRAITS_USERSTATUS_INVALID;
		String[] params = new String[] { stringValue };
		LOGGER.error(key.getMessage(params));
		throw new BipRuntimeException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
	}

}
