package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpFeignRuntimeExceptionTest {

	private static final String TEST_KEY = "test.key";
	private static final String TEST_TEXT = "text for test.key";
	private static final HttpStatus TEST_HTTP_STATUS = HttpStatus.BAD_REQUEST;
	private static final MessageSeverity TEST_SEVERITY = MessageSeverity.ERROR;

	@Test
	public void instantiateOcpFeignRuntimeExceptionTest() throws Exception {
		OcpFeignRuntimeException ocpFeignRuntimeException =
				new OcpFeignRuntimeException(TEST_KEY, TEST_TEXT, TEST_SEVERITY, TEST_HTTP_STATUS);

		Assert.assertTrue(ocpFeignRuntimeException.getKey().equals(TEST_KEY));
		Assert.assertTrue(ocpFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(ocpFeignRuntimeException.getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(ocpFeignRuntimeException.getSeverity().equals(TEST_SEVERITY));
	}

	@Test
	public void instantiateUsingOtherConstructorOcpFeignRuntimeExceptionTest() throws Exception {
		OcpFeignRuntimeException ocpFeignRuntimeException = new OcpFeignRuntimeException(TEST_KEY, TEST_TEXT, TEST_SEVERITY,
				TEST_HTTP_STATUS, new Exception("test wrapped error"));

		Assert.assertTrue(ocpFeignRuntimeException.getKey().equals(TEST_KEY));
		Assert.assertTrue(ocpFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(ocpFeignRuntimeException.getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(ocpFeignRuntimeException.getSeverity().equals(TEST_SEVERITY));
	}

}
