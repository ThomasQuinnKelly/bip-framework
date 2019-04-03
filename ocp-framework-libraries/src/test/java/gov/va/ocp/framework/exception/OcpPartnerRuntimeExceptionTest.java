package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpPartnerRuntimeExceptionTest {

	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@Test
	public void initializeOcpPartnerRuntimeExceptionTest() {
		assertNotNull(new OcpPartnerRuntimeException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
	}

}
