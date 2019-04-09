package gov.va.bip.framework.rest.provider.aspect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.audit.ResponseAuditData;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * This is the base class for REST provider aspects.
 * It provides the Point Cuts to be used by extending classes that declare types of @Aspect advice.
 *
 * @author jshrader
 */
public class BaseHttpProviderAspect extends BaseAsyncAudit {

	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BaseHttpProviderAspect.class);

	/**
	 * Protected constructor.
	 */
	protected BaseHttpProviderAspect() {
		super();
	}

	/**
	 * This point cut selects any code within a REST controller class that...
	 * <ol>
	 * <li>is annotated with org.springframework.web.bind.annotation.RestController
	 * </ol>
	 */
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	protected static final void restController() {
		// Do nothing.
	}

	/**
	 * This point cut selects REST endpoint operations that are of interest to external consumers.
	 * Those are operations that...
	 * <ol>
	 * <li>are in a rest controller class (see the {@link #restController()} pointcut)
	 * <li>that are public in scope
	 * <li>where the method returns {@code gov.va.bip.framework.transfer.ProviderTransferObjectMarker+} or
	 * {@code org.springframework.http.ResponseEntity<gov.va.bip.framework.transfer.ProviderTransferObjectMarker+>}
	 * </ol>
	 */
	@Pointcut("restController() && ("
			+ "execution(public org.springframework.http.ResponseEntity<gov.va.bip.framework.transfer.ProviderTransferObjectMarker+> *(..))"
			+ " || execution(public gov.va.bip.framework.transfer.ProviderTransferObjectMarker+ *(..))"
			+ ")")
	protected static final void publicServiceResponseRestMethod() {
		// Do nothing.
	}

	/**
	 * This point cut selects code (e.g. methods) that ...
	 * <ol>
	 * <li>are annotated with gov.va.bip.framework.audit.Auditable
	 * </ol>
	 */
	@Pointcut("@annotation(gov.va.bip.framework.audit.Auditable)")
	protected static final void auditableAnnotation() {
		// Do nothing.
	}

	/**
	 * This point cut selects code (e.g. methods) that...
	 * <ol>
	 * <li>are annotated with gov.va.bip.framework.audit.Auditable - see {@link #auditableAnnotation()}
	 * <li>and, only at the time when the code inside the annotated method is executed
	 * </ol>
	 */
	@Pointcut("auditableAnnotation() && execution(* *(..))")
	protected static final void auditableExecution() {
		// Do nothing.
	}

	/**
	 * Write audit for response.
	 *
	 * @param response the response
	 * @param auditEventData the auditable annotation
	 */
	protected void writeResponseAudit(final Object response, final AuditEventData auditEventData,
			final MessageSeverity messageSeverity,
			final Throwable t) {

		final HttpServletResponse httpServletReponse =
				((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

		final ResponseAuditData responseAuditData = new ResponseAuditData();

		if (httpServletReponse != null) {
			getHttpResponseAuditData(httpServletReponse, responseAuditData);
		}

		if (response != null) {
			responseAuditData.setResponse(response);
		}

		if (super.getAsyncLogger() != null) {
			LOGGER.debug("Invoking AuditLogSerializer.asyncLogRequestResponseAspectAuditData()");
			super.getAsyncLogger().asyncLogRequestResponseAspectAuditData(auditEventData, responseAuditData, ResponseAuditData.class,
					messageSeverity, t);
		}
	}

	private void getHttpResponseAuditData(final HttpServletResponse httpServletReponse, final ResponseAuditData responseAuditData) {
		final Map<String, String> headers = new HashMap<>();
		final Collection<String> headerNames = httpServletReponse.getHeaderNames();
		populateHeadersMap(httpServletReponse, headers, headerNames);
		responseAuditData.setHeaders(headers);
	}

	private void populateHeadersMap(final HttpServletResponse httpServletResponse, final Map<String, String> headersToBePopulated,
			final Collection<String> listOfHeaderNames) {
		for (final String headerName : listOfHeaderNames) {
			String value;
			value = httpServletResponse.getHeader(headerName);
			headersToBePopulated.put(headerName, value);
		}
	}
}
