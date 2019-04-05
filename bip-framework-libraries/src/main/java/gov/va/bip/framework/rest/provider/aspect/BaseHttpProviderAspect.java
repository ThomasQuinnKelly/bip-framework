package gov.va.bip.framework.rest.provider.aspect;

import java.io.IOException;
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

import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.RequestAuditData;
import gov.va.bip.framework.audit.ResponseAuditData;
import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.util.SanitizationUtil;

/**
 * This is the base class for REST provider aspects.
 * It provides the Point Cuts to be used by extending classes that declare types of @Aspect advice.
 *
 * @author jshrader
 */
public class BaseHttpProviderAspect {

	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BaseHttpProviderAspect.class);

	/** How many bytes of an uploaded file will be read for inclusion in the audit record */
	private static final int NUMBER_OF_BYTES = 1024;

	@Autowired
	AuditLogSerializer asyncLogging;

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
	 * Write audit for request.
	 *
	 * @param request the request
	 * @param auditEventData the auditable annotation
	 */
	protected void writeRequestInfoAudit(final List<Object> request, final AuditEventData auditEventData) {

		LOGGER.debug("RequestContextHolder.getRequestAttributes() {}", RequestContextHolder.getRequestAttributes());

		final RequestAuditData requestAuditData = new RequestAuditData();

		// set request on audit data
		if (request != null) {
			requestAuditData.setRequest(request);
		}

		final HttpServletRequest httpServletRequest =
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		if (httpServletRequest != null) {
			getHttpRequestAuditData(httpServletRequest, requestAuditData);
		}

		LOGGER.debug("RequestAuditData: {}", requestAuditData.toString());

		if (asyncLogging != null) {
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, requestAuditData, RequestAuditData.class,
					MessageSeverity.INFO, null);
		}
	}

	/**
	 * Add request header information, and any multipart/form or multipart/mixed data, to the audit data.
	 *
	 * @param httpServletRequest the servlet request
	 * @param requestAuditData the audit data object
	 */
	private void getHttpRequestAuditData(final HttpServletRequest httpServletRequest, final RequestAuditData requestAuditData) {
		final Map<String, String> headers = new HashMap<>();

		ArrayList<String> listOfHeaderNames = Collections.list(httpServletRequest.getHeaderNames());
		populateHeadersMap(httpServletRequest, headers, listOfHeaderNames);

		requestAuditData.setHeaders(headers);
		requestAuditData.setUri(httpServletRequest.getRequestURI());
		requestAuditData.setMethod(httpServletRequest.getMethod());

		final String contentType = httpServletRequest.getContentType();

		LOGGER.debug("Content Type: {}", SanitizationUtil.stripXSS(contentType));

		if (contentType != null && (contentType.toLowerCase(Locale.ENGLISH).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)
				|| contentType.toLowerCase(Locale.ENGLISH).startsWith(BipConstants.MIME_MULTIPART_MIXED))) {
			final List<String> attachmentTextList = new ArrayList<>();
			InputStream inputstream = null;
			try {
				for (final Part part : httpServletRequest.getParts()) {
					final Map<String, String> partHeaders = new HashMap<>();
					populateHeadersMap(part, partHeaders, part.getHeaderNames());
					inputstream = part.getInputStream();
					
					attachmentTextList.add(partHeaders.toString() + ", " + convertBytesToString(inputstream));
					closeInputStreamIfRequired(inputstream);
				}
			} catch (final Exception ex) {
				LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
						"Error occurred while reading the upload file. {}", ex);

			} finally {
				if (inputstream != null) {
					try {
						inputstream.close();
					} catch (IOException e) {
						LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
								"Error occurred while closing the upload file. {}", e);
					}
				}
			}
			requestAuditData.setAttachmentTextList(attachmentTextList);
			requestAuditData.setRequest(null);
		}
	}

	private void closeInputStreamIfRequired(InputStream inputstream) throws IOException {
		if (inputstream != null) {
			inputstream.close();
		}
	}

	private void populateHeadersMap(final HttpServletRequest httpServletRequest, final Map<String, String> headersToBePopulated,
			final Collection<String> listOfHeaderNames) {
		for (final String headerName : listOfHeaderNames) {
			String value;
			value = httpServletRequest.getHeader(headerName);
			headersToBePopulated.put(headerName, value);
		}
	}

	private void populateHeadersMap(final Part part, final Map<String, String> headersToBePopulated,
			final Collection<String> listOfHeaderNames) {
		for (final String headerName : listOfHeaderNames) {
			String value;
			value = part.getHeader(headerName);
			headersToBePopulated.put(headerName, value);
		}
	}

	/**
	 * Read the first 1024 bytes and convert that into a string.
	 *
	 * @param in the input stream
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static String convertBytesToString(final InputStream in) throws IOException {
		int offset = 0;
		int bytesRead = 0;
		final byte[] data = new byte[NUMBER_OF_BYTES];
		while ((bytesRead = in.read(data, offset, data.length - offset)) != -1) {
			offset += bytesRead;
			if (offset >= data.length) {
				break;
			}
		}
		return new String(data, 0, offset, "UTF-8");
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

		if (asyncLogging != null) {
			LOGGER.debug("Invoking AuditLogSerializer.asyncLogRequestResponseAspectAuditData()");
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, responseAuditData, ResponseAuditData.class,
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
