package gov.va.bip.framework.security.jwt.correlation;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

public enum Issuers {

	/** The VHA assigning authority */
	USVHA("USVHA"),
	/** The VBA assigning authority */
	USVBA("USVBA"),
	/** The DOD assigning authority */
	USDOD("USDOD");

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(Issuers.class);

	/** The arbitrary string value of the enumeration */
	private String issuer;

	/**
	 * Private constructor for enum initialization
	 *
	 * @param issuer String
	 */
	private Issuers(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * The arbitrary String value assigned to the enumeration.
	 *
	 * @return String
	 */
	public String value() {
		return this.issuer;
	}

	/**
	 * Get the enumeration for the associated arbitrary String value.
	 * throws a runtime exception if the string value does not match one of the enumeration values.
	 *
	 * @param stringValue the string value
	 * @return Issuers - the enumeration
	 * @throws BipRuntimeException if no match of enumeration values
	 */
	public static Issuers fromValue(final String stringValue) {
		for (Issuers s : Issuers.values()) {
			if (s.value().equals(stringValue)) {
				return s;
			}
		}
		MessageKeys key = MessageKeys.BIP_SECURITY_TRAITS_ISSUER_INVALID;
		String[] params = new String[] { stringValue };
		LOGGER.error(key.getMessage(params));
		throw new BipRuntimeException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
	}

}
