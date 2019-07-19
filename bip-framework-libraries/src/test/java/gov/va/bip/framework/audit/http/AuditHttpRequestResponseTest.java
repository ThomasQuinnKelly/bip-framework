package gov.va.bip.framework.audit.http;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.core.io.Resource;
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
	public void getHttpRequestAuditDataTestWithOctetStream() {
		AuditHttpRequestResponse auditHttpRequestResponse = new AuditHttpRequestResponse();
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpRequestAuditData requestAuditData = mock(HttpRequestAuditData.class);
		String[] stringArray = new String[] { "string1" };
		Set<String> set = new HashSet<>();
		set.addAll(Arrays.asList(stringArray));
		Enumeration<String> enumeration = new Vector<String>(set).elements();
		when(httpServletRequest.getHeaderNames()).thenReturn(enumeration);
		when(httpServletRequest.getContentType()).thenReturn(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		List<Object> requests =  new LinkedList<>();
		Resource mockResource = mock(Resource.class);
		InputStream inputStream = new ByteArrayInputStream("test string2".getBytes());
		try {
			when(mockResource.getInputStream()).thenReturn(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unable to mock Resource");
		}
		requests.add(mockResource);
		ReflectionTestUtils.invokeMethod(auditHttpRequestResponse.new AuditHttpServletRequest(), "getHttpRequestAuditData",
				httpServletRequest, requestAuditData, requests);
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
