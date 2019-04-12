package gov.va.bip.framework.audit;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class BaseAsyncAuditTest {

	@Test
	public void postConstructTest() {
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		AuditLogSerializer auditLogSerializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", auditLogSerializer);
		baseAsyncAudit.postConstruct();
	}
}
