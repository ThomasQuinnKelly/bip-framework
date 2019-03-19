package gov.va.ocp.framework.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.messages.MessageSeverity;

public class ServiceMessageTest {

	@Test
	public void testEmptyConstructor() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage();
		assertNull(serviceMessage.getKey());
		assertNull(serviceMessage.getSeverity());
		assertNull(serviceMessage.getText());
	}

	@Test
	public void testSeverityKeyConstructor() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage(MessageSeverity.ERROR, "UnitTestKey", "ServiceMessage text", HttpStatus.BAD_REQUEST);
		assertEquals(MessageSeverity.ERROR, serviceMessage.getSeverity());
		assertEquals("UnitTestKey", serviceMessage.getKey());
	}

	@Test
	public void testSeverityKeyTextConstructor() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null);
		assertEquals(MessageSeverity.WARN, serviceMessage.getSeverity());
		assertEquals("UnitTestKey", serviceMessage.getKey());
		assertEquals("TextMsg", serviceMessage.getText());
	}

	@Test
	public void testParamsConstructor() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null,
				1, new String[] { "0" }, new String[] { "1" });
		assertEquals(new Integer(1), serviceMessage.getParamCount());
		assertArrayEquals(new String[] { "0" }, serviceMessage.getParamNames());
		assertArrayEquals(new String[] { "1" }, serviceMessage.getParamValues());

		serviceMessage.setParamCount(2);
		serviceMessage.setParamNames(new String[] { "0" });
		serviceMessage.setParamValues(new String[] { "1" });
	}

	@Test
	public void testParamsOnlyConstructor() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage(1, new String[] { "0" }, new String[] { "1" });
		assertEquals(new Integer(1), serviceMessage.getParamCount());
		assertArrayEquals(new String[] { "0" }, serviceMessage.getParamNames());
		assertArrayEquals(new String[] { "1" }, serviceMessage.getParamValues());
		assertNull(serviceMessage.getHttpStatus());
	}

	@Test
	public void testSetters() throws Exception {
		ServiceMessage serviceMessage = new ServiceMessage(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null);
		assertEquals(MessageSeverity.WARN, serviceMessage.getSeverity());
		assertEquals("UnitTestKey", serviceMessage.getKey());
		assertEquals("TextMsg", serviceMessage.getText());
		serviceMessage.setKey("UpdatedKey");
		serviceMessage.setSeverity(MessageSeverity.FATAL);
		serviceMessage.setText("UpdatedText");
		assertEquals(MessageSeverity.FATAL, serviceMessage.getSeverity());
		assertEquals("UpdatedKey", serviceMessage.getKey());
		assertEquals("UpdatedText", serviceMessage.getText());
	}

	@Test
	public void testEquals() throws Exception {
		ServiceMessage message1 = new ServiceMessage(MessageSeverity.INFO, "UnitTestKey", "TextMsg", null);
		ServiceMessage message2 = new ServiceMessage(MessageSeverity.INFO, "UnitTestKey", "Not included in equals determination", null);
		assertTrue(message1.equals(message2));
	}

	@Test
	public void testSetStatus() throws Exception {
		ServiceMessage message1 = new ServiceMessage(MessageSeverity.INFO, "UnitTestKey", "TextMsg", null);
		message1.setHttpStatus(HttpStatus.BAD_REQUEST);
		assertTrue(message1.getHttpStatus() == HttpStatus.BAD_REQUEST);
		assertNotNull(message1.getStatus());
	}

	@Test
	public void testMessageSeverityValueOf() throws Exception {

		assertEquals(MessageSeverity.WARN, MessageSeverity.fromValue("WARN"));
		assertEquals("WARN", MessageSeverity.WARN.value());
	}

}