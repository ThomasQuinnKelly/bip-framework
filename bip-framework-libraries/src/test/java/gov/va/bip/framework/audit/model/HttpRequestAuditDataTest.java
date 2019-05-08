package gov.va.bip.framework.audit.model;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

public class HttpRequestAuditDataTest {
	@Test
	public void toStringTest() {
		HttpRequestAuditData httpRequestAuditData = new HttpRequestAuditData();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("testKey", "testValue");
		httpRequestAuditData.setHeaders(headers);
		String request = "test request";
		List<Object> requestList = new LinkedList<>();
		requestList.add(request);
		httpRequestAuditData.setRequest(requestList);
		assertTrue(httpRequestAuditData.toString()
				.equals("HttpRequestAuditData{headers=" + ReflectionToStringBuilder.toString(headers) +
						", uri='" + httpRequestAuditData.getUri() + "\'" + ", method='" + httpRequestAuditData.getMethod() + "\'" + ", " + requestList + "}"));
	}

	@Test
	public void toStringWithNullHeadersTest() {
		HttpRequestAuditData httpRequestAuditData = new HttpRequestAuditData();
		httpRequestAuditData.setHeaders(null);
		assertTrue(httpRequestAuditData.toString().equals("HttpRequestAuditData{headers=, uri='null', method='null', []}"));
	}
}
