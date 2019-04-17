package gov.va.bip.framework.audit;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import gov.va.bip.framework.audit.model.HttpRequestAuditData;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;
import gov.va.bip.framework.audit.model.RequestAuditData;
import gov.va.bip.framework.audit.model.ResponseAuditData;
import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.validation.Defense;

/**
 * Performs simple audit logging on any type of request or response objects.
 *
 * @author aburkholder
 */
@Component
public class BaseAsyncAudit {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BaseAsyncAudit.class);

	private static final String INTERNAL_EXCEPTION_PREFIX = "Error ServiceMessage: ";

	/** How many bytes of an uploaded file will be read for inclusion in the audit record */
	public static final int NUMBER_OF_BYTES = 1024;

	@Autowired
	AuditLogSerializer auditLogSerializer;

	/**
	 * Instantiate the class.
	 */
	public BaseAsyncAudit() {
		super();
	}

	/**
	 * Make sure the class was initialized properly
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(auditLogSerializer);
	}

	/**
	 * Get the asynchronous logger to initiate logging audit data.
	 *
	 * @return AuditLogSerializer
	 */
	public AuditLogSerializer getAsyncLogger() {
		return auditLogSerializer;
	}

	/**
	 * Write any kind of request object list to the audit logs.
	 *
	 * @param request - the list of request objects
	 * @param requestAuditData - the {@link AuditableData} container to put the request in
	 * @param auditEventData - the audit meta-data for the event
	 * @param severity - the Message Severity, if {@code null} then MessageSeverity.INFO is used
	 * @param t - a throwable, if relevant (may be {@code null})
	 */
	public void writeRequestAuditLog(final List<Object> request, final RequestAuditData requestAuditData,
			final AuditEventData auditEventData, final MessageSeverity severity, final Throwable t) {
		if (request != null) {
			requestAuditData.setRequest(request);
		}

		LOGGER.debug("RequestAuditData: {}", requestAuditData.toString());

		getAsyncLogger().asyncAuditRequestResponseData(auditEventData, requestAuditData, HttpRequestAuditData.class,
				severity == null ? MessageSeverity.INFO : severity, t);
	}

	/**
	 * Write any kind of response Object to the audit logs.
	 *
	 * @param response - the response object
	 * @param responseAuditData - the {@link AuditableData} container to put the response in
	 * @param auditEventData - the audit meta-data for the event
	 * @param severity - the Message Severity, if {@code null} then MessageSeverity.INFO is used
	 * @param t - a throwable, if relevant (may be {@code null})
	 */
	public void writeResponseAuditLog(final Object response, final ResponseAuditData responseAuditData,
			final AuditEventData auditEventData,
			final MessageSeverity severity, final Throwable t) {
		if (response != null) {
			responseAuditData.setResponse(response);
		}

		LOGGER.debug("Invoking AuditLogSerializer.asyncLogRequestResponseAspectAuditData()");
		getAsyncLogger().asyncAuditRequestResponseData(auditEventData, responseAuditData,
				HttpResponseAuditData.class, severity == null ? MessageSeverity.INFO : severity, t);
	}

	/**
	 * Read the first 1024 bytes and convert that into a string.
	 *
	 * @param in the input stream
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String convertBytesToString(final InputStream in) throws IOException {
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
	 * Attempt to close an input stream.
	 *
	 * @param inputstream
	 * @throws IOException
	 */
	public static void closeInputStreamIfRequired(final InputStream inputstream) {
		if (inputstream != null) {
			try {
				inputstream.close();
			} catch (Exception e) { // NOSONAR intentionally broad catch
				LOGGER.debug("Problem closing input stream.", e);
			}
		}
	}

	/**
	 * Standard handling of exceptions that are thrown from within the advice (not exceptions thrown by application code, such
	 * exceptions are rethrown).
	 *
	 * @param adviceName the name of the advice/method in which the exception was thrown
	 * @param attemptingTo the attempted task that threw the exception
	 * @param auditEventData the audit event data object
	 * @param throwable the exception that was thrown
	 */
	public void handleInternalExceptionAndRethrowApplicationExceptions(final String adviceName, final String attemptingTo,
			final AuditEventData auditEventData, MessageKeys key, final Throwable throwable) {

		try {
			MessageKeys effectiveKey = key == null ? MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION : key;
			LOGGER.error(effectiveKey.getMessage(adviceName, attemptingTo), throwable);
			final BipRuntimeException bipRuntimeException = new BipRuntimeException(
					effectiveKey, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable,
					adviceName, attemptingTo);

			AuditLogger.error(auditEventData,
					INTERNAL_EXCEPTION_PREFIX + bipRuntimeException.getMessage(),
					bipRuntimeException);

			throw bipRuntimeException;

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			handleAnyRethrownExceptions(adviceName, e);
		}
	}

	/**
	 * If - after attempting to audit an internal error - another exception is thrown,
	 * then put the whole mess in an error log (non-audit), and throw the exception again
	 * as a Runtime exception.
	 *
	 * @param adviceName the name of the advice/method in which the exception was thrown
	 * @param e the unexpected exception
	 * @throws RuntimeException
	 */
	private void handleAnyRethrownExceptions(final String adviceName, final Throwable e) {

		String msg = adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.";
		LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);

		RuntimeException ise = null;
		if (!RuntimeException.class.isAssignableFrom(e.getClass())) {
			ise = new IllegalStateException(msg);
		} else {
			ise = (RuntimeException) e;
		}
		throw ise;
	}
}
