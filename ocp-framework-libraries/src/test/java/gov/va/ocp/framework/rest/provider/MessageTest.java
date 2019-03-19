package gov.va.ocp.framework.rest.provider;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;
import org.springframework.http.HttpStatus;

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
		assertTrue(message.getHttpStatus(400).equals(HttpStatus.resolve(400)));
	}
}
