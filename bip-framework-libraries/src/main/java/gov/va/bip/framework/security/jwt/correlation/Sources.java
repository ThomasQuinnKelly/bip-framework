package gov.va.bip.framework.security.jwt.correlation;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

public enum Sources {
	/** The ICN assigning facility (VHA) */
	ICN("200M"),
	/** The DOD assigning facility */
	USDOD("200DOD"),
	/** The BiRLS assigning facility (VBA) */
	BIRLS("200BRLS"),
	/** The CORP facility */
	CORP("200CORP");

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(Sources.class);

	/** The arbitrary string value of the enumeration */
	private String source;

	/**
	 * Private constructor for enum initialization
	 *
	 * @param source String
	 */
	private Sources(String source) {
		this.source = source;
	}

	/**
	 * The arbitrary String value assigned to the enumeration.
	 *
	 * @return String
	 */
	public String value() {
		return this.source;
	}

	/**
	 * Get the enumeration for the associated arbitrary String value.
	 * Throws a runtime exception if the string value does not match one of the enumeration values.
	 *
	 * @param stringValue the string value
	 * @return Sources - the enumeration
	 * @throws BipRuntimeException if no match of enumeration values
	 */
	public static Sources fromValue(final String stringValue) {
		for (Sources s : Sources.values()) {
			if (s.value().equals(stringValue)) {
				return s;
			}
		}
		MessageKeys key = MessageKeys.BIP_SECURITY_TRAITS_SOURCE_INVALID;
		Object[] params = new Object[] { stringValue };
		LOGGER.error(key.getMessage(params));
		throw new BipRuntimeException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
	}
}
