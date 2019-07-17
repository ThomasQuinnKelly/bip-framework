package gov.va.bip.framework.audit.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.audit.model.HttpRequestAuditData;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;

public class AuditHttpRequestResponseTest {

	@Test
	public void getHttpRequestAuditDataTest() {
		AuditHttpRequestResponse auditHttpRequestResponse = new AuditHttpRequestResponse();
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpRequestAuditData requestAuditData = mock(HttpRequestAuditData.class);
		String[] stringArray = new String[] { "string1" };
		Set<String> set = new HashSet<>();
		set.addAll(Arrays.asList(stringArray));
		Enumeration<String> enumeration = new Vector<String>(set).elements();
		when(httpServletRequest.getHeaderNames()).thenReturn(enumeration);
		when(httpServletRequest.getContentType()).thenReturn(MediaType.MULTIPART_FORM_DATA_VALUE);
		ReflectionTestUtils.invokeMethod(auditHttpRequestResponse.new AuditHttpServletRequest(),
				"getHttpRequestAuditData", httpServletRequest, requestAuditData, null);
		verify(requestAuditData, times(1)).setAttachmentTextList(any());
		verify(requestAuditData, times(1)).setRequest(any());
	}

	@Test
	public void getHttpResponseAuditDataTest() {
		AuditHttpRequestResponse auditHttpRequestResponse = new AuditHttpRequestResponse();
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		HttpResponseAuditData responseAuditData = mock(HttpResponseAuditData.class);
		Collection<String> enumeration = new ArrayList<String>();
		enumeration.add(HttpHeaders.CONTENT_TYPE);
		when(httpServletResponse.getHeaderNames()).thenReturn(enumeration);
		when(httpServletResponse.getContentType()).thenReturn(MediaType.TEXT_HTML_VALUE);
		ReflectionTestUtils.invokeMethod(auditHttpRequestResponse.new AuditHttpServletResponse(),
				"getHttpResponseAuditData", httpServletResponse, responseAuditData);
		verify(responseAuditData, times(1)).setHeaders(any());
	}
}
