package gov.va.bip.framework.audit;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.bip.framework.audit.model.RequestAuditData;
import gov.va.bip.framework.audit.model.ResponseAuditData;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageSeverity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/*
@RunWith(SpringRunner.class)
public class RequestResponseLogSerializerTest {

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;
	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	@Spy
	ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private RequestResponseLogSerializer requestResponseLogSerializer = new RequestResponseLogSerializer();

	RequestAuditData requestAuditData = new RequestAuditData();

	ResponseAuditData responseAuditData = new ResponseAuditData();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().addAppender(mockAppender);

		requestAuditData.setRequest(Arrays.asList("Request"));
		//requestAuditData.setMethod("GET");
		//requestAuditData.setUri("/");
		//requestAuditData.setAttachmentTextList(new ArrayList<String>(Arrays.asList("attachment1", "attachment2")));
		Map<String, String> headers = new HashMap<>();
		headers.put("Header1", "Header1Value");
		//requestAuditData.setHeaders(headers);

		responseAuditData.setResponse("Response");
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ReflectionTestUtils.setField(requestResponseLogSerializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss");
	}

	@SuppressWarnings("unchecked")
	@After
	public void teardown() {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().detachAppender(mockAppender);
	}
}

 */
