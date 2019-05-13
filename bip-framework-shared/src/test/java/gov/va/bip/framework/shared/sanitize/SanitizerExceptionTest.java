package gov.va.bip.framework.shared.sanitize;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;



public class SanitizerExceptionTest {

	private static final String TEST_WRAPPED_MESSAGE = "test wrapped message";
	private static final String TEST_MESSAGE = "test message";

	@Test
	public void initializeSanitizeExceptionTest() {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		SanitizerException e = new SanitizerException(TEST_MESSAGE);
		PrintStream stream = new PrintStream(outStream);
		e.printStackTrace(stream);
		assertTrue(outStream.toString().contains(TEST_MESSAGE));
	}

	@Test
	public void initializeWithWrappedSanitizeExceptionTest() {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		SanitizerException e = new SanitizerException(TEST_MESSAGE, new Exception(TEST_WRAPPED_MESSAGE));
		PrintStream stream = new PrintStream(outStream);
		e.printStackTrace(stream);
		assertTrue(outStream.toString().contains(TEST_MESSAGE));
		assertTrue(outStream.toString().contains(TEST_WRAPPED_MESSAGE));

	}
	
	@Test
	public void initializeSanitizeThrowableExceptionTest() throws Exception {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Throwable cause = new Throwable(TEST_MESSAGE);
		SanitizerException e = new SanitizerException(cause);
		PrintStream stream = new PrintStream(outStream);
		e.printStackTrace(stream);
		assertTrue(outStream.toString().contains(TEST_MESSAGE));
	}
	
	@Test
	public void initializationNoArgTest() {
		assertNotNull(new SanitizerException());
	}
}
