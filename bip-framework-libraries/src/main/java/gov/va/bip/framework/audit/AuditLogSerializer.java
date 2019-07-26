package gov.va.bip.framework.audit;

import static gov.va.bip.framework.audit.BaseAsyncAudit.NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import gov.va.bip.framework.audit.model.HttpRequestAuditData;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;
import gov.va.bip.framework.audit.model.RequestAuditData;
import gov.va.bip.framework.audit.model.ResponseAuditData;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;

/**
 * The purpose of this class is to asynchronuously serialize an object to JSON
 * and then write it to the audit logs.
 *
 * @author npaulus
 * @author akulkarni
 */
@Component
public class AuditLogSerializer implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(AuditLogSerializer.class);

	/** Jackson object mapper */
	ObjectMapper mapper = new ObjectMapper();

	/** Format for Java to XML conversions */
	@Value("${spring.jackson.date-format:yyyy-MM-dd'T'HH:mm:ss.SSSZ}")
	private String dateFormat;

	public AuditLogSerializer() {
		super();
	}

	/**
	 * Asynchronuously converts an object to JSON and then writes it to the audit logger.
	 * <p>
	 * <b>"Around" Advised by:</b>
	 * org.springframework.cloud.sleuth.instrument.async.TraceAsyncAspect.traceBackgroundThread(org.aspectj.lang.ProceedingJoinPoint)
	 *
	 * @param auditEventData Data specific to the audit event
	 * @param auditData The request and response audit data
	 */
	@Async
	public void asyncAuditRequestResponseData(final AuditEventData auditEventData, final AuditableData auditData,
			final Class<?> auditDataClass, final MessageSeverity messageSeverity, final Throwable t) {

		List<Object> objectList = null;

		try {
			if ((auditData instanceof RequestAuditData) && !(auditData instanceof HttpRequestAuditData)) {
				objectList = ((RequestAuditData) auditData).getRequest();
				List<Object> newObjectList = restrictObjectsToSetByteLimit(objectList);
				((RequestAuditData) auditData).setRequest(newObjectList);
			} else if ((auditData instanceof ResponseAuditData) && !(auditData instanceof HttpResponseAuditData)) {
				objectList = new LinkedList<>();
				objectList.add(((ResponseAuditData) auditData).getResponse());
				List<Object> newObjectList = restrictObjectsToSetByteLimit(objectList);
				((ResponseAuditData) auditData).setResponse(newObjectList.get(0));
			}
		} catch (Exception ex) {
			LOGGER.error("Audit Logging failed as the objects to be logged could not be restricted to set limit of "
					+ NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT + " bytes", ex);
			return;
		}

		String auditDetails = null;
		if (auditData != null) {
			try {
				mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
				mapper.setDateFormat(new SimpleDateFormat(dateFormat, Locale.US));

				mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
				mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);

				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);
				mapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);

				auditDetails = mapper.writeValueAsString(auditData);
			} catch (JsonProcessingException ex) {
				LOGGER.error("Error occurred on ClassCast or JSON processing, calling custom toString()", ex);
				try {
					auditDetails = auditDataClass.cast(auditData).toString();
				} catch (Exception e) {
					LOGGER.error("Error occurred on ClassCast or Custom toString() processing, calling ReflectionToStringBuilder", e);
					auditDetails = ReflectionToStringBuilder.toString(auditData, ToStringStyle.JSON_STYLE, false, false, Object.class);
				}
			}
		}

		if (messageSeverity.equals(MessageSeverity.ERROR) || messageSeverity.equals(MessageSeverity.FATAL)) {
			AuditLogger.error(auditEventData, auditDetails, t);
		} else if (messageSeverity.equals(MessageSeverity.WARN)) {
			AuditLogger.warn(auditEventData, auditDetails);
		} else {
			AuditLogger.info(auditEventData, auditDetails);
		}
	}

	private List<Object> restrictObjectsToSetByteLimit(final List<Object> objectList) throws Exception {
		List<Object> newObjectList = new LinkedList<>();
		for (Object object : objectList) {
			try {
				byte[] bytesAfterLimiting = null;
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream so = new ObjectOutputStream(bo);
				so.writeObject(object);
				if (bo.size() > NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT) {
					bytesAfterLimiting = new byte[NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT];
					bo.write(bytesAfterLimiting);
					newObjectList.add(bytesAfterLimiting);
				} else {
					newObjectList.add(object);
				}
				so.flush();
			} catch (Exception ex) {
				LOGGER.error("Error occurred while trying to get object size by converting the object into a stream!", ex);
				throw ex;
			}
		}
		return newObjectList;
	}

	/**
	 * Asynchronuously writes to the audit logger.
	 * <p>
	 * <b>"Around" Advised by:</b>
	 * org.springframework.cloud.sleuth.instrument.async.TraceAsyncAspect.traceBackgroundThread(org.aspectj.lang.ProceedingJoinPoint)
	 *
	 * @param auditEventData Data specific to the audit event
	 * @param messageSeverity the message severity
	 * @param activityDetail the activity detail
	 */
	@Async
	public void asyncAuditMessageData(final AuditEventData auditEventData, final String activityDetail,
			final MessageSeverity messageSeverity, final Throwable t) {

		if (messageSeverity.equals(MessageSeverity.ERROR) || messageSeverity.equals(MessageSeverity.FATAL)) {
			AuditLogger.error(auditEventData, activityDetail, t);
		} else if (messageSeverity.equals(MessageSeverity.WARN)) {
			AuditLogger.warn(auditEventData, activityDetail);
		} else {
			AuditLogger.info(auditEventData, activityDetail);
		}
	}

}
