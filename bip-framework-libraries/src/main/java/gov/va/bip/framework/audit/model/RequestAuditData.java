package gov.va.bip.framework.audit.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import gov.va.bip.framework.audit.AuditableData;

/**
 * An {@link AuditableData} transfer object for sending any request Object list to the audit logger.
 *
 * @author aburkholder
 */
public class RequestAuditData implements Serializable, AuditableData {
	private static final long serialVersionUID = -6463691536690649662L;

	/* The request. */
	private transient List<Object> request = Collections.emptyList();

	/**
	 * Gets the request that is being logged in the audit logs.
	 *
	 * @return the request
	 */
	public List<Object> getRequest() {
		return request;
	}

	/**
	 * Set the request object to be logged in the audit logs.
	 *
	 * @param request
	 */
	public void setRequest(final List<Object> request) {
		this.request = request;
	}

	/**
	 * Manually formatted JSON-like string of key/value pairs.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "RequestAuditData{request=" + (request == null ? "" : ReflectionToStringBuilder.toString(request)) + '}';
	}
}
