package gov.va.bip.framework.audit.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.audit.model.HttpRequestAuditData;

public class AuditHttpRequestResponseTest {

	@Test
	public void getHttpRequestAuditDataTest() {
		AuditHttpRequestResponse auditHttpRequestResponse = new AuditHttpRequestResponse();
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpRequestAuditData requestAuditData = mock(HttpRequestAuditData.class);
		String[] stringArray = new String[] {"string1"};
		Set<String> set = new HashSet<>();
		set.addAll(Arrays.asList(stringArray));
		Enumeration<String> enumeration = new Vector<String>(set).elements();
		when(httpServletRequest.getHeaderNames()).thenReturn(enumeration);
		when(httpServletRequest.getContentType()).thenReturn(MediaType.MULTIPART_FORM_DATA_VALUE);
		ReflectionTestUtils.invokeMethod(auditHttpRequestResponse.new AuditHttpServletRequest(), "getHttpRequestAuditData",
				httpServletRequest, requestAuditData);
		verify(requestAuditData, times(1)).setAttachmentTextList(any());
		verify(requestAuditData, times(1)).setRequest(any());
	}
}
