package gov.va.bip.framework.exception;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class SqsExceptionTest {

	private MessageKey var1;
	private MessageSeverity var2;
	private HttpStatus var3;
	private Throwable var4;
	private String var5 = "text";


	@Test
	public void instantiateBaseSqsExceptions() throws Exception {

		SqsException sqsException = new SqsException(var1, var2, var3, var4, var5 = null);
	}

}
