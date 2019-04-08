package gov.va.bip.framework.audit;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.util.SanitizationUtil;
import gov.va.bip.framework.validation.Defense;

public class BaseAsyncAudit {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BaseAsyncAudit.class);

	/** How many bytes of an uploaded file will be read for inclusion in the audit record */
	private static final int NUMBER_OF_BYTES = 1024;

	@Autowired
	AuditLogSerializer asyncLogging;

	/**
	 * Protected constructor.
	 */
	protected BaseAsyncAudit() {
		super();
	}

	@PostConstruct
	public void postConstruct() {
		Defense.notNull(asyncLogging);
	}

	/**
	 * Write audit log for a request.
	 *
	 * @param request the request
	 * @param auditEventData the auditable annotation
	 */
	protected void writeRequestAuditLog(final List<Object> request, final AuditEventData auditEventData) {

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

		asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, requestAuditData, RequestAuditData.class,
				MessageSeverity.INFO, null);
	}

	/**
	 * The asynchronous audit log serializer.
	 *
	 * @return AuditLogSerializer
	 */
	protected AuditLogSerializer getAsyncLogger() {
		return this.asyncLogging;
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

	/**
	 * Copies headers in the servlet request into a Map.
	 *
	 * @param httpServletRequest
	 * @param headersToBePopulated
	 * @param listOfHeaderNames
	 */
	private void populateHeadersMap(final HttpServletRequest httpServletRequest, final Map<String, String> headersToBePopulated,
			final Collection<String> listOfHeaderNames) {
		for (final String headerName : listOfHeaderNames) {
			String value;
			value = httpServletRequest.getHeader(headerName);
			headersToBePopulated.put(headerName, value);
		}
	}

	/**
	 * Copies headers from the Part into a Map.
	 *
	 * @param part
	 * @param headersToBePopulated
	 * @param listOfHeaderNames
	 */
	private void populateHeadersMap(final Part part, final Map<String, String> headersToBePopulated,
			final Collection<String> listOfHeaderNames) {
		for (final String headerName : listOfHeaderNames) {
			String value;
			value = part.getHeader(headerName);
			headersToBePopulated.put(headerName, value);
		}
	}

	/**
	 * Attempt to close an input stream.
	 *
	 * @param inputstream
	 * @throws IOException
	 */
	private void closeInputStreamIfRequired(InputStream inputstream) throws IOException {
		if (inputstream != null) {
			try {
				inputstream.close();
			} catch (Exception e) { // NOSONAR
				// NOSONAR nothing to be done here
			}
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

}
