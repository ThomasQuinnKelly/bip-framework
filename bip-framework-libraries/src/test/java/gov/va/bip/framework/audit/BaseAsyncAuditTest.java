package gov.va.bip.framework.audit;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;

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

	@Test
	public void closeInputStreamIfRequiredTest() {
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		InputStream mockInputstream = mock(InputStream.class);
		baseAsyncAudit.closeInputStreamIfRequired(mockInputstream);
		try {
			verify(mockInputstream, times(1)).close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Problem testing input stream closing");
		}
	}
}
