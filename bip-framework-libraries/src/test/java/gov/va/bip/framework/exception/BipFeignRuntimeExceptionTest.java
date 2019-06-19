package gov.va.bip.framework.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.bip.framework.exception.BipFeignRuntimeException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;

@RunWith(SpringJUnit4ClassRunner.class)
public class BipFeignRuntimeExceptionTest {

	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;
	private static final String TEST_TEXT = "NO_KEY";
	private static final HttpStatus TEST_HTTP_STATUS = HttpStatus.BAD_REQUEST;
	private static final MessageSeverity TEST_SEVERITY = MessageSeverity.ERROR;

	@Test
	public void instantiateBipFeignRuntimeExceptionTest() throws Exception {
		BipFeignRuntimeException bipFeignRuntimeException =
				new BipFeignRuntimeException(TEST_KEY, TEST_SEVERITY, TEST_HTTP_STATUS);

		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getKey().equals(TEST_KEY.getKey()));
		Assert.assertTrue(bipFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getSeverity().equals(TEST_SEVERITY));
	}

	@Test
	public void instantiateUsingOtherConstructorBipFeignRuntimeExceptionTest() throws Exception {
		BipFeignRuntimeException bipFeignRuntimeException =
				new BipFeignRuntimeException(TEST_KEY, TEST_SEVERITY,
						TEST_HTTP_STATUS, new Exception("test wrapped error"));

		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getKey().equals(TEST_KEY.getKey()));
		Assert.assertTrue(bipFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(bipFeignRuntimeException.getExceptionData().getSeverity().equals(TEST_SEVERITY));
	}

}
