package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpValidationRuntimeExceptionTest {

	@Test
	public void initializeOcpValidationRuntimeExceptionTest() {
		assertNotNull(new OcpValidationRuntimeException("test key", "test message", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(new OcpValidationRuntimeException("test key", "test message", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
				new Exception()));
	}
}
