package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpExceptionTest {

	private static final String TEST_MESSAGE = "testMessage";
	private static final String TEST_KEY = "testKey";

	@Test
	public void initializeOcpExceptionTest() {
		assertNotNull(new OcpException(TEST_KEY, TEST_MESSAGE, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(new OcpException(TEST_KEY, TEST_MESSAGE, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
				new Exception("wrapped message")));
	}

	@Test
	public void getterTest() {
		OcpException ocpException = new OcpException(TEST_KEY, TEST_MESSAGE, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
				new Exception("wrapped message"));
		assertTrue(ocpException.getKey().equals(TEST_KEY));
		assertTrue(ocpException.getMessage().equals(TEST_MESSAGE));
		assertTrue(ocpException.getSeverity().equals(MessageSeverity.ERROR));
		assertTrue(ocpException.getStatus().equals(HttpStatus.BAD_REQUEST));
	}

}
