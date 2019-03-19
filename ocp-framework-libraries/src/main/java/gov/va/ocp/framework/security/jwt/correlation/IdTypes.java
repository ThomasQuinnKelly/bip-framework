package gov.va.ocp.framework.security.jwt.correlation;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

public enum IdTypes {
	/** The type of correlation id for national authorities */
	NATIONAL("NI"),
	/** The type of correlation id for patient authorities */
	PATIENT("PI"),
	/** The pnidType (currently the only one) associated with the PNID field value */
	SOCIAL("SS");

	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(IdTypes.class);

	/** The arbitrary string value of the enumeration */
	private String type;

	/**
	 * Private constructor for enum initialization
	 *
	 * @param type String
	 */
	private IdTypes(String type) {
		this.type = type;
	}

	/**
	 * The arbitrary String value assigned to the enumeration.
	 *
	 * @return String
	 */
	public String value() {
		return this.type;
	}

	/**
	 * Get the enumeration for the associated arbitrary String value.
	 * Throws a runtime exception if the string value does not match one of the enumeration values.
	 *
	 * @param stringValue the string value
	 * @return IdTypes - the enumeration
	 * @throws OcpRuntimeException if no match of enumeration values
	 */
	public static IdTypes fromValue(final String stringValue) {
		for (IdTypes s : IdTypes.values()) {
			if (s.value().equals(stringValue)) {
				return s;
			}
		}
		String msg = "IdType {} does not exist: " + stringValue;
		LOGGER.error(msg);
		throw new OcpRuntimeException("", msg, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
	}
}
