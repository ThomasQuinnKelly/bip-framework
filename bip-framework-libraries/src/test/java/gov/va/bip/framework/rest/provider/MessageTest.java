package gov.va.bip.framework.rest.provider;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MessageTest {

	private static final String TEST_TEXT = "test text";

	@Test
	public void testInitializationAndGetter() {
		Message message = new Message("ERROR", "test key", TEST_TEXT, 400);
		Date testTimeStamp = Date.from(Instant.now());
		message.setTimestamp(testTimeStamp);
		assertTrue(message.getTimestamp().equals(testTimeStamp));
		assertTrue(message.getStatus().equals("400"));
		assertTrue(message.getText().equals(TEST_TEXT));
		assertTrue(message.getHttpStatus().equals(HttpStatus.resolve(400)));
		assertTrue(message.getHttpStatus(400).equals(HttpStatus.resolve(400)));
	}

	@Test
	public void testGetStatusWhenNull() throws Exception {
		Message message1 = new Message("ERROR", "test key", TEST_TEXT, null);
		assertNull(message1.getStatus());
	}
}
