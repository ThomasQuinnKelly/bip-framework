package gov.va.bip.framework.audit;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

import gov.va.bip.framework.audit.model.HttpResponseAuditData;

public class ResponseAuditDataTest {

	@Test
	public void toStringTest() {
		HttpResponseAuditData responseAuditData = new HttpResponseAuditData();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("testKey", "testValue");
		responseAuditData.setHeaders(headers);
		String response = "test response";
		responseAuditData.setResponse(response);
		assertTrue(responseAuditData.toString().equals("HttpResponseAuditData{headers=" + ReflectionToStringBuilder.toString(headers)
		+ ", uri=" + ", response=" + response + "}"));
	}

	@Test
	public void toStringWithNullHeadersTest() {
		HttpResponseAuditData responseAuditData = new HttpResponseAuditData();
		responseAuditData.setHeaders(null);
		assertTrue(responseAuditData.toString().equals("HttpResponseAuditData{headers=, uri=, response=}"));
	}
}
