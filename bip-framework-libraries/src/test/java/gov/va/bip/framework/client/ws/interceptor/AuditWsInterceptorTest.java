package gov.va.bip.framework.client.ws.interceptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.client.ws.interceptor.AuditWsInterceptor;
import gov.va.bip.framework.client.ws.interceptor.AuditWsInterceptorConfig;
import gov.va.bip.framework.client.ws.interceptor.transport.ByteArrayTransportOutputStream;
import gov.va.bip.framework.exception.BipPartnerRuntimeException;

public class AuditWsInterceptorTest {

	AuditWsInterceptor interceptor = new AuditWsInterceptor(AuditWsInterceptorConfig.AFTER);
	MessageContext messageContext = mock(MessageContext.class);
	WebServiceMessage webServiceMessage = new WebServiceMessage() {

		@Override
		public void writeTo(final OutputStream outputStream) throws IOException {
			((ByteArrayTransportOutputStream) outputStream).write("test xml message".getBytes());
		}

		@Override
		public Source getPayloadSource() {
			return null;
		}

		@Override
		public Result getPayloadResult() {
			return null;
		}
	};
	AuditWsInterceptorConfig.AuditWsMetadata auditWsMetaData = mock(AuditWsInterceptorConfig.AuditWsMetadata.class);
	AuditEventData auditServiceEventData = new AuditEventData(AuditEvents.SERVICE_AUDIT, "MethodName", "ClassName");

	@Test
	public void handleRequestTest() {
		assertTrue(interceptor.handleRequest(messageContext));
	}

	@Test
	public void handleResponseTest() {
		assertTrue(interceptor.handleResponse(messageContext));
	}

	@Test
	public void handleFaultTest() {
		assertTrue(interceptor.handleFault(messageContext));
	}

	@Test
	public void afterCompletionTest() {
		interceptor.afterCompletion(messageContext, new Exception());
		verify(messageContext, times(1)).getRequest();
		verify(messageContext, times(2)).getResponse();
	}

	@Test
	public void doAuditTest() {
		when(auditWsMetaData.eventData()).thenReturn(auditServiceEventData);
		when(auditWsMetaData.messagePrefix()).thenReturn("test prefix value");
		ReflectionTestUtils.invokeMethod(interceptor, "doAudit", auditWsMetaData, webServiceMessage);
	}

	@Test
	public void getXmlTest() {
		assertTrue(ReflectionTestUtils.invokeMethod(interceptor, "getXml", webServiceMessage).equals("test xml message"));
	}

	@Test(expected = BipPartnerRuntimeException.class)
	public void handleInternalErrorTest() {
		ReflectionTestUtils.invokeMethod(interceptor, "handleInternalError", AuditEvents.PARTNER_SOAP_REQUEST, "test audit Activity",
				new Exception());
	}

	@Test
	public void writeAuditErrorTest() {
		ReflectionTestUtils.invokeMethod(interceptor, "writeAuditError", "test advice name", new Exception(),
				new AuditEventData(AuditEvents.PARTNER_SOAP_REQUEST, "test activity", "test audited name"));
	}

}
