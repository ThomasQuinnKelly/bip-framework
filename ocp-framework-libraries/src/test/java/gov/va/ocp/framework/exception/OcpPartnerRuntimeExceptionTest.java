package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpPartnerRuntimeExceptionTest {

	@Test
	public void initializeOcpPartnerRuntimeExceptionTest() {
		assertNotNull(new OcpPartnerRuntimeException("test key", "test message", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));

	}

}
