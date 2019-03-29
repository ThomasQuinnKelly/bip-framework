package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpPartnerExceptionTest {

	@Test
	public void initializeOcpPartnerExceptionTest() {
		assertNotNull(new OcpPartnerException("test key", "test message", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(
				new OcpPartnerException("test key", "test message", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Exception()));
	}

}
