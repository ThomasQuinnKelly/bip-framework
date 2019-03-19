package gov.va.ocp.framework.security.jwt.correlation;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

public enum Issuers {

	/** The VHA assigning authority */
	USVHA("USVHA"),
	/** The VBA assigning authority */
	USVBA("USVBA"),
	/** The DOD assigning authority */
	USDOD("USDOD");

	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(Issuers.class);

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
	 * @throws OcpRuntimeException if no match of enumeration values
	 */
	public static Issuers fromValue(final String stringValue) {
		for (Issuers s : Issuers.values()) {
			if (s.value().equals(stringValue)) {
				return s;
			}
		}
		String msg = "Issuer {} does not exist: " + stringValue;
		LOGGER.error(msg);
		throw new OcpRuntimeException("", msg, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
	}

}
