package gov.va.bip.framework.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.service.BipServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
public class BipServiceExceptionTest {

	private static final String TEST_KEY_MESSAGE = "NO_KEY";
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmptyConstructor() {
		BipServiceException bipServiceException = new BipServiceException(TEST_KEY, null, null, null, new String[] { null });
		assertTrue(bipServiceException.getMessage().equals(TEST_KEY_MESSAGE));
		assertNull(bipServiceException.getExceptionData().getSeverity());
		assertNull(bipServiceException.getExceptionData().getStatus());
		assertNull(bipServiceException.getCause());
	}

	@Test
	public void testPopulatedConstructor() {
		BipServiceException bipServiceException =
				new BipServiceException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Throwable("test"));
		assertNotNull(bipServiceException.getExceptionData().getKey());
		assertNotNull(bipServiceException.getMessage());
		assertNotNull(bipServiceException.getExceptionData().getSeverity());
		assertNotNull(bipServiceException.getExceptionData().getStatus());
		assertNotNull(bipServiceException.getCause());
	}

	@Test
	public void testPopulatedNoCauseConstructor() {
		BipServiceException bipServiceException =
				new BipServiceException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		assertNotNull(bipServiceException.getExceptionData().getKey());
		assertNotNull(bipServiceException.getMessage());
		assertNotNull(bipServiceException.getExceptionData().getSeverity());
		assertNotNull(bipServiceException.getExceptionData().getStatus());
		assertNull(bipServiceException.getCause());
	}

}
