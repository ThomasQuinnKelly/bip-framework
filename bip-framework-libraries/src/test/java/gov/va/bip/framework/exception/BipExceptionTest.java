package gov.va.bip.framework.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.bip.framework.exception.BipException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

@RunWith(SpringJUnit4ClassRunner.class)
public class BipExceptionTest {
	private static final String TEST_VALUE = "test value";
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@Test
	public void initializeBipExceptionTest() {
		assertNotNull(new BipException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(new BipException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Exception("wrapped message")));
		assertNotNull(new BipException(null, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Exception("wrapped message")));
	}

	@Test
	public void getterTest() {
		String[] params = new String[] { "param1", "param2" };
		BipException bipException =
				new BipException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Exception("wrapped message"), params);
		assertTrue(bipException.getExceptionData().getKey().equals(TEST_KEY.getKey()));
		assertTrue(bipException.getMessage().equals(TEST_KEY.getKey()));
		assertTrue(bipException.getExceptionData().getSeverity().equals(MessageSeverity.ERROR));
		assertTrue(bipException.getExceptionData().getParams().equals(params));
		assertTrue(bipException.getExceptionData().getStatus().equals(HttpStatus.BAD_REQUEST));
		System.setProperty("server.name", TEST_VALUE);
		assertTrue(bipException.getExceptionData().getServerName().equals(System.getProperty("server.name")));
	}

}
