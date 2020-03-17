package gov.va.bip.framework.exception;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertNotNull;

public class BipPartnerExceptionTest {

	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@Test
	public void initializeBipPartnerExceptionTest() {
		assertNotNull(new BipPartnerException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(
				new BipPartnerException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Exception()));
	}

}
