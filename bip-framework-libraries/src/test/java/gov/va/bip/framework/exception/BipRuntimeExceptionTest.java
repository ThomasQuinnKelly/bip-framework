package gov.va.bip.framework.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;

public class BipRuntimeExceptionTest {

	private static final String TEST_KEY_MESSAGE = "NO_KEY";
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;
	private static final String SERVER_NAME_PROPERTY = "server.name";

	@Before
	public void setUp() {
		System.setProperty(SERVER_NAME_PROPERTY, "Test Server");
	}

	@BeforeClass
	public static void setUpClass() {
		System.setProperty(SERVER_NAME_PROPERTY, "Test Server");
	}

	@Test
	public void getMessageTestServerNameNull() throws Exception {
		BipRuntimeException bipRuntimeException =
				new BipRuntimeException(TEST_KEY, null, null, (String[]) null);
		Assert.assertTrue(bipRuntimeException.getExceptionData().getServerName().equals(System.getProperty("server.name")));
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		BipRuntimeException bipRuntimeException = new BipRuntimeException(TEST_KEY, null, null, (String[]) null);
		Assert.assertTrue(bipRuntimeException.getMessage().equals(TEST_KEY_MESSAGE));
	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		BipRuntimeException bipRuntimeException = new BipRuntimeException(TEST_KEY, null, null, cause);
		Assert.assertTrue(bipRuntimeException.getMessage().equals(TEST_KEY_MESSAGE));
	}

	@Test
	public void initializationWithNullKeyTest() {
		assertNotNull(new BipRuntimeException(null, null, null, (String[]) null));
	}
}
