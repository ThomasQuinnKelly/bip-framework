package gov.va.bip.framework.audit;

import static gov.va.bip.framework.audit.BaseAsyncAudit.NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import gov.va.bip.framework.audit.model.HttpRequestAuditData;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;

@RunWith(SpringRunner.class)
public class AuditLogSerializerTest {

	private static final String MESSAGE_STARTSWITH = "Error occurred on ClassCast or JSON processing, calling";

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;
	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	@Spy
	ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private AuditLogSerializer auditLogSerializer = new AuditLogSerializer();

	AuditEventData auditEventData = new AuditEventData(AuditEvents.API_REST_REQUEST, "MethodName", "ClassName");

	AuditEventData auditServiceEventData = new AuditEventData(AuditEvents.SERVICE_AUDIT, "MethodName", "ClassName");

	HttpRequestAuditData requestAuditData = new HttpRequestAuditData();

	HttpResponseAuditData responseAuditData = new HttpResponseAuditData();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().addAppender(mockAppender);

		requestAuditData.setRequest(Arrays.asList("Request"));
		requestAuditData.setMethod("GET");
		requestAuditData.setUri("/");
		requestAuditData.setAttachmentTextList(new ArrayList<String>(Arrays.asList("attachment1", "attachment2")));
		Map<String, String> headers = new HashMap<>();
		headers.put("Header1", "Header1Value");
		requestAuditData.setHeaders(headers);

		responseAuditData.setResponse("Response");
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ReflectionTestUtils.setField(auditLogSerializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss");
	}

	@SuppressWarnings("unchecked")
	@After
	public void teardown() {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().detachAppender(mockAppender);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJson() throws Exception {
		auditLogSerializer.asyncAuditRequestResponseData(auditEventData, requestAuditData, HttpRequestAuditData.class,
				MessageSeverity.INFO, null);
		auditLogSerializer.asyncAuditRequestResponseData(auditEventData, responseAuditData, HttpResponseAuditData.class,
				MessageSeverity.INFO, null);
		verify(mockAppender, atLeastOnce()).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents =
				captorLoggingEvent.getAllValues().stream().filter(x -> x.getLevel() == Level.INFO).collect(Collectors.toList());
		final String expectedRequest = String.valueOf(BufferRecyclers.getJsonStringEncoder().quoteAsString(
				"{\"request\":[\"Request\"],\"headers\":{\"Header1\":\"Header1Value\"},\"uri\":\"/\",\"method\":\"GET\",\"attachmentTextList\":[\"attachment1\",\"attachment2\"]}"));
		final String expectedResponse =
				String.valueOf(BufferRecyclers.getJsonStringEncoder().quoteAsString("{\"response\":\"Response\"}"));
		assertEquals(expectedRequest, loggingEvents.get(0).getMessage());
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.INFO));
		assertEquals(expectedResponse, loggingEvents.get(1).getMessage());
		assertThat(loggingEvents.get(1).getLevel(), is(ch.qos.logback.classic.Level.INFO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonException() throws Exception {
		when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
		auditLogSerializer.asyncAuditRequestResponseData(auditEventData, requestAuditData, HttpRequestAuditData.class,
				MessageSeverity.INFO, null);
		verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		assertTrue(loggingEvents.get(0).getMessage().startsWith(MESSAGE_STARTSWITH));
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
		assertTrue((loggingEvents.get(1).getMessage() != null)
				&& loggingEvents.get(1).getMessage().startsWith("HttpRequestAuditData{headers="));
		assertThat(loggingEvents.get(1).getLevel(), is(ch.qos.logback.classic.Level.INFO));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonExceptionError() throws Exception {
		when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
		auditLogSerializer.asyncAuditRequestResponseData(auditEventData, requestAuditData, HttpRequestAuditData.class,
				MessageSeverity.ERROR, new Exception());
		verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		assertTrue(loggingEvents.get(0).getMessage().startsWith(MESSAGE_STARTSWITH));
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
		assertTrue((loggingEvents.get(1).getMessage() != null)
				&& loggingEvents.get(1).getMessage().startsWith("HttpRequestAuditData{headers="));
		assertThat(loggingEvents.get(1).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonExceptionFatal() throws Exception {
		when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
		auditLogSerializer.asyncAuditRequestResponseData(auditEventData, requestAuditData, HttpRequestAuditData.class,
				MessageSeverity.FATAL, new Exception());
		verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		assertTrue(loggingEvents.get(0).getMessage().startsWith(MESSAGE_STARTSWITH));
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
		assertTrue((loggingEvents.get(1).getMessage() != null)
				&& loggingEvents.get(1).getMessage().startsWith("HttpRequestAuditData{headers="));
		assertThat(loggingEvents.get(1).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testServiceMessage() throws Exception {
		auditLogSerializer.asyncAuditMessageData(auditServiceEventData, "This is test", MessageSeverity.INFO, null);

		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		Assert.assertEquals("This is test", loggingEvents.get(0).getMessage());
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.INFO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testServiceMessageError() throws Exception {
		auditLogSerializer.asyncAuditMessageData(auditServiceEventData, "Error test", MessageSeverity.ERROR,
				new Exception());

		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		Assert.assertTrue(loggingEvents.get(0).getMessage().startsWith("Error test"));
		assertThat(loggingEvents.get(0).getLevel(), is(ch.qos.logback.classic.Level.ERROR));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void restrictObjectsToSetByteLimit_largefile() {
		List<Object> request = new LinkedList<>();
		String file1Mb = "/testFiles/1MbFile.txt";
		URL url = this.getClass().getResource(file1Mb);
		try {
			request.add(IOUtils.toByteArray(url.openStream()));
		} catch (IOException e1) {
			fail("failed to read file data");
		}
		// Class filterClass = AuditLogSerializer.class.getDeclaredClasses()[0].set newInstance()
		List<Object> returnList =
				(List<Object>) ReflectionTestUtils.invokeMethod(auditLogSerializer, "restrictObjectsToSetByteLimit", request);
		assertTrue(returnList.get(0) instanceof byte[]);
		assertTrue(((byte[]) returnList.get(0)).length == NUMBER_OF_BYTES_TO_LIMIT_AUDIT_LOGGED_OBJECT);

	}

}
