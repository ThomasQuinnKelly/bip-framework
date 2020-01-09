package gov.va.bip.framework.audit.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class MessageAuditDataTest {

	@Test
	public void toStringWithNullMessageTest() {
		MessageAuditData messageAuditData = new MessageAuditData();
		messageAuditData.setMessage(null);
		assertNull(messageAuditData.getMessage());
		assertTrue(messageAuditData.toString().equals("MessageAuditData{" + '}'));
	}
	
	@Test
	public void toStringWithNotNullMessageTest() {
		MessageAuditData messageAuditData = new MessageAuditData();
		messageAuditData.setMessage(Arrays.asList("Test Message"));
		assertNotNull(messageAuditData.toString());
	}

}
