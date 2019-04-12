package gov.va.bip.framework.audit.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import gov.va.bip.framework.audit.AuditableData;

/**
 * An {@link AuditableData} transfer response object for sending any Object to the audit logger.
 *
 * @author aburkholder
 */
public class ResponseAuditData implements Serializable, AuditableData {
	private static final long serialVersionUID = -5812100176075217636L;

	/* The response. */
	private transient Object response;

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public Object getResponse() {
		return response;
	}

	/**
	 * Sets the response.
	 *
	 * @param response
	 */
	public void setResponse(final Object response) {
		this.response = response;
	}

	/**
	 * Manually formatted JSON-like string of key/value pairs.
	 */
	@Override
	public String toString() {
		return "ResponseAuditData{response=" + (getResponse() == null ? "" : ReflectionToStringBuilder.toString(getResponse())) + '}';
	}
}
