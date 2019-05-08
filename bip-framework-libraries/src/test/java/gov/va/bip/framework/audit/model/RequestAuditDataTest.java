package gov.va.bip.framework.audit.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RequestAuditDataTest {

	@Test
	public void toStringWithNullRequestTest() {
		RequestAuditData auditData = new RequestAuditData();
		auditData.setRequest(null);
		assertTrue(auditData.toString().equals("RequestAuditData{request=" + '}'));
	}

}
