package gov.va.bip.framework.exception;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BipRuntimeExceptionTest {

	@BeforeClass
	public static void setUp() {
		System.setProperty("server.name", "Test Server");
	}

	@Test
	public void instantiateBaseBipExceptions() throws Exception {
		BipRuntimeException bipRuntimeException = new BipRuntimeException();

		Assert.assertEquals("Test Server", bipRuntimeException.getServerName());
	}

	@Test
	public void getMessageTestServerName() throws Exception {
		BipRuntimeException bipRuntimeException = new BipRuntimeException();

		Assert.assertEquals(null, bipRuntimeException.getMessage());

	}

	@Test
	public void getMessageTestServerNameNull() throws Exception {
		// setup
		// do crazy reflection to make server name null
		Field field = BipRuntimeException.class.getDeclaredField("SERVER_NAME");
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.isAccessible();
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.isAccessible();
		field.setAccessible(true);
		field.set(null, null);

		BipRuntimeException bipRuntimeException = new BipRuntimeException();

		Assert.assertNull(bipRuntimeException.getMessage());

		// Reset server name to Test Server
		field.set(null, "Test Server");
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		BipRuntimeException bipRuntimeException = new BipRuntimeException();
		Assert.assertEquals(null, bipRuntimeException.getMessage());

	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		BipRuntimeException bipRuntimeException = new BipRuntimeException("Test Message", cause);
		Assert.assertEquals("Test Message", bipRuntimeException.getMessage());

	}
}
