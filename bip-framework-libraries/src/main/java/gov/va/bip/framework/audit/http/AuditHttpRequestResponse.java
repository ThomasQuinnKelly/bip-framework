package gov.va.bip.framework.audit.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditLogger;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.audit.model.HttpRequestAuditData;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;
import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.ProviderResponse;
import gov.va.bip.framework.shared.sanitize.Sanitizer;
import gov.va.bip.framework.util.HttpHeadersUtil;

/**
 * Performs audit logging specifically for HttpServlet request/response objects.
 *
 * @author aburkholder
 */
@Component
public class AuditHttpRequestResponse {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(AuditHttpRequestResponse.class);

	@Autowired
	protected BaseAsyncAudit baseAsyncAudit;

	/**
	 * Protected constructor.
	 */
	public AuditHttpRequestResponse() {
		super();
	}

	/**
	 * Provides access to audit operations related to logging the servlet Request in a fluent way.
	 *
	 * @return AuditServletRequest - the container for request audit operations
	 */
	public AuditHttpServletRequest auditServletRequest() {
		return new AuditHttpServletRequest();
	}

	/**
	 * Container class for audit operations related to logging the servlet Request.
	 *
	 * @author aburkholder
	 */
	public class AuditHttpServletRequest {

		/**
		 * Write audit log for HTTP request.
		 *
		 * @param request - the request object
		 * @param auditEventData - the audit meta-data for the event
		 */
		public void writeHttpRequestAuditLog(final List<Object> request, final AuditEventData auditEventData) {

			LOGGER.debug("RequestContextHolder.getRequestAttributes() {}", RequestContextHolder.getRequestAttributes());

			final HttpRequestAuditData requestAuditData = new HttpRequestAuditData();

			final HttpServletRequest httpServletRequest =
					((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			if (httpServletRequest != null) {
				getHttpRequestAuditData(httpServletRequest, requestAuditData);
			}

			baseAsyncAudit.writeRequestAuditLog(request, requestAuditData, auditEventData, MessageSeverity.INFO, null);
		}

		/**
		 * Add request header information, and any multipart/form or multipart/mixed data, to the audit data.
		 *
		 * @param httpServletRequest the servlet request
		 * @param requestAuditData the audit data object
		 */
		private void getHttpRequestAuditData(final HttpServletRequest httpServletRequest,
				final HttpRequestAuditData requestAuditData) {

			ArrayList<String> listOfHeaderNames = Collections.list(httpServletRequest.getHeaderNames());
			final Map<String, String> headers = populateRequestHeadersMap(httpServletRequest, listOfHeaderNames);

			requestAuditData.setHeaders(headers);
			requestAuditData.setUri(httpServletRequest.getRequestURI());
			requestAuditData.setMethod(httpServletRequest.getMethod());

			final String contentType = httpServletRequest.getContentType();

			LOGGER.debug("Content Type: {}", Sanitizer.stripXss(contentType));

			if ((contentType != null) && (contentType.toLowerCase(Locale.ENGLISH).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)
					|| contentType.toLowerCase(Locale.ENGLISH).startsWith(BipConstants.MIME_MULTIPART_MIXED))) {

				final List<String> attachmentTextList = getMultipartHeaders(httpServletRequest);
				requestAuditData.setAttachmentTextList(attachmentTextList);
				requestAuditData.setRequest(null);
			}
		}

		/**
		 * Add request multipart/form or multipart/mixed header information to the audit data.
		 *
		 * @param httpServletRequest the servlet request
		 * @return List of the headers in key/value string format
		 */
		private List<String> getMultipartHeaders(final HttpServletRequest httpServletRequest) {
			final List<String> multipartHeaders = new ArrayList<>();
			InputStream inputstream = null;
			try {
				for (final Part part : httpServletRequest.getParts()) {
					final Map<String, String> partHeaders = new HashMap<>();
					for (final String headerName : part.getHeaderNames()) {
						String value;
						value = part.getHeader(headerName);
						partHeaders.put(headerName, value);
					}

					try {
						inputstream = part.getInputStream();
						multipartHeaders
								.add(partHeaders.toString() + ", " + BaseAsyncAudit.convertBytesToString(inputstream));
					} finally {
						BaseAsyncAudit.closeInputStreamIfRequired(inputstream);
					}
				}
			} catch (final Exception ex) {
				LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
						"Error occurred while reading the upload file. {}", ex);
			}
			return multipartHeaders;
		}

		/**
		 * Copies headers in the servlet request into a Map.
		 *
		 * @param httpServletRequest
		 * @param headersToBePopulated
		 * @param listOfHeaderNames
		 */
		private Map<String, String> populateRequestHeadersMap(final HttpServletRequest httpServletRequest,
				final Collection<String> listOfHeaderNames) {

			final Map<String, String> headersToBePopulated = new HashMap<>();
			for (final String headerName : listOfHeaderNames) {
				String value;
				value = httpServletRequest.getHeader(headerName);
				headersToBePopulated.put(headerName, value);
			}
			return headersToBePopulated;
		}

	}

	/**
	 * Provides access to audit operations related to logging the servlet Response in a fluent way.
	 *
	 * @return AuditServletResponse - the container for response audit operations
	 */
	public AuditHttpServletResponse auditServletResponse() {
		return new AuditHttpServletResponse();
	}

	/**
	 * Provides access to audit operations related to logging the servlet Response in a fluent way.
	 *
	 * @return AuditServletResponse - the container for response audit operations
	 * @author aburkholder
	 */
	public class AuditHttpServletResponse {

		/**
		 * Write audit log for HTTP response.
		 *
		 * @param response - the HTTP response
		 * @param auditEventData - the audit event meta-data
		 * @param severity - the Message Severity, if {@code null} then MessageSeverity.INFO is used
		 * @param t - a throwable, if relevant (may be {@code null})
		 */
		public void writeHttpResponseAuditLog(final Object response, final AuditEventData auditEventData,
				final MessageSeverity severity, final Throwable t) {

			final HttpServletResponse httpServletReponse =
					((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

			final HttpResponseAuditData responseAuditData = new HttpResponseAuditData();

			if (httpServletReponse != null) {
				getHttpResponseAuditData(httpServletReponse, responseAuditData);
			}

			baseAsyncAudit.writeResponseAuditLog(response, responseAuditData, auditEventData, severity, t);
		}

		/**
		 * Add response header information to the audit data.
		 *
		 * @param httpServletReponse the servlet response
		 * @param responseAuditData the container to put the header info in
		 */
		private void getHttpResponseAuditData(final HttpServletResponse httpServletResponse,
				final HttpResponseAuditData responseAuditData) {
			final Map<String, String> headers = new HashMap<>();
			final Collection<String> headerNames = httpServletResponse.getHeaderNames();

			for (final String headerName : headerNames) {
				String value;
				value = httpServletResponse.getHeader(headerName);
				headers.put(headerName, value);
			}

			responseAuditData.setHeaders(headers);
		}
	}

	/**
	 * Standard handling of exceptions that are thrown from within the advice
	 * (not exceptions thrown by application code).
	 *
	 * @param adviceName the name of the advice method in which the exception was thrown
	 * @param attemptingTo the attempted task that threw the exception
	 * @param auditEventData the audit event data object
	 * @param throwable the exception that was thrown
	 */
	protected ResponseEntity<ProviderResponse> handleInternalException(final String adviceName, final String attemptingTo,
			final AuditEventData auditEventData, final Throwable throwable) {
		try {
			MessageKeys key = MessageKeys.BIP_AUDIT_ASPECT_ERROR_UNEXPECTED;
			final BipRuntimeException bipRuntimeException = new BipRuntimeException(key,
					MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable, adviceName, attemptingTo);

			String msg = "Error ServiceMessage: " + bipRuntimeException;
			AuditLogger.error(auditEventData, msg, bipRuntimeException);
			LOGGER.error(adviceName + " auditing uncaught exception.", bipRuntimeException);

			final ProviderResponse providerResponse = new ProviderResponse();
			providerResponse.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
					bipRuntimeException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

			return new ResponseEntity<>(providerResponse, HttpHeadersUtil.buildHttpHeadersForError(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			return handleAnyRethrownExceptions(adviceName, throwable, e);
		}
	}

	/**
	 * If audit is having troubles, this is the last resort for logging and returning meaningful
	 * exception information.
	 *
	 * @param adviceName - the advice from which the audit is being called
	 * @param originatingThrowable - the exception that started all of this
	 * @param e - the current exception
	 * @return ResponseEntity - the ProviderResponse with available information
	 */
	private ResponseEntity<ProviderResponse> handleAnyRethrownExceptions(final String adviceName, final Throwable originatingThrowable,
			final Throwable e) {
		ResponseEntity<ProviderResponse> entity;
		MessageKeys key = MessageKeys.BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT;
		String msg = key.getMessage(adviceName, originatingThrowable.getClass().getSimpleName());
		LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);

		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				msg, HttpStatus.INTERNAL_SERVER_ERROR);
		entity = new ResponseEntity<>(body, HttpHeadersUtil.buildHttpHeadersForError(), HttpStatus.INTERNAL_SERVER_ERROR);
		return entity;
	}

}
