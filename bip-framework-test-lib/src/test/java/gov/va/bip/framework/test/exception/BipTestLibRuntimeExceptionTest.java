package gov.va.bip.framework.test.exception;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class BipTestLibRuntimeExceptionTest {

	private static final String TEST_WRAPPED_MESSAGE = "test wrapped message";
	private static final String TEST_MESSAGE = "test message";

	@Test
	public void initializeBipTestLibRuntimeExceptionTest() {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		BipTestLibRuntimeException e = new BipTestLibRuntimeException(TEST_MESSAGE);
		PrintStream stream = new PrintStream(outStream);
		e.printStackTrace(stream);
		assertTrue(outStream.toString().contains(TEST_MESSAGE));
	}

	@Test
	public void initializeWithWrappedExceptionBipTestLibRuntimeExceptionTest() {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		BipTestLibRuntimeException e = new BipTestLibRuntimeException(TEST_MESSAGE, new Exception(TEST_WRAPPED_MESSAGE));
		PrintStream stream = new PrintStream(outStream);
		e.printStackTrace(stream);
		assertTrue(outStream.toString().contains(TEST_MESSAGE));
		assertTrue(outStream.toString().contains(TEST_WRAPPED_MESSAGE));

	}
}
