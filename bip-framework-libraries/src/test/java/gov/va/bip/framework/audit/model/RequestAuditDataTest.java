package gov.va.bip.framework.audit.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RequestAuditDataTest {

	@Test
	public void toStringWithNullRequestTest() {
		RequestAuditData auditData = new RequestAuditData();
		auditData.setRequest(null);
		assertTrue(auditData.toString().equals("RequestAuditData{request=" + '}'));
	}

}
