package gov.va.ocp.framework.audit;

/**
 * Events types to be associated with {@link AuditEventData} used in {@link Auditable} classes and/or methods.
 * Created by vgadda on 8/17/17.
 */
public enum AuditEvents {
	/** Cached response event while getting from the cache */
	CACHED_SERVICE_RESPONSE("cachedResponse"),
	/** Partner client REST request event */
	PARTNER_REST_REQUEST("restserviceRequest"),
	/** Partner client REST response event */
	PARTNER_REST_RESPONSE("restserviceResponse"),
	/** Partner client SOAP request event */
	PARTNER_SOAP_REQUEST("webserviceRequest"),
	/** Partner client SOAP response event */
	PARTNER_SOAP_RESPONSE("webserviceResponse"),
	/** Partner client SOAP response event */
	PARTNER_SOAP_FAULT("webserviceFault"),
	/** REST request event at the micro-service application's REST API */
	API_REST_REQUEST("apiRestRequest"),
	/** REST response event at the micro-service application's REST API */
	API_REST_RESPONSE("apiRestResponse"),
	/** Security interceptor or similar event */
	SECURITY("securityAudit"),
	/** Generic audit event from within the service business tier */
	SERVICE_AUDIT("serviceAudit"),
	/** An audit rquest was made, but no event type was specified */
	UNKNOWN("unknown");

	/** A default value that can be used for the Audit "activity" string */
	private String defaultActivity;

	/**
	 * Constructor for AuditEvents enumerations.
	 *
	 * @param defaultActivity - convenience default description of the audit activity for the enumeration
	 */
	AuditEvents(String defaultActivity) {
		this.defaultActivity = defaultActivity;
	}

	/**
	 * Get the convenience default description of the audit activity for this audit event enumeration.
	 *
	 * @return String - the default activity for the audit event
	 */
	public String getDefaultActivity() {
		return this.defaultActivity;
	}
}
