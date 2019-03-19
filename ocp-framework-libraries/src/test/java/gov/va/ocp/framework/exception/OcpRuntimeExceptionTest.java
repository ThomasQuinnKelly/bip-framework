package gov.va.ocp.framework.exception;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OcpRuntimeExceptionTest {

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
		// setup
		// do crazy reflection to make server name null
		Field field = OcpExceptionExtender.class.getDeclaredField("SERVER_NAME");
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.isAccessible();
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.isAccessible();
		field.setAccessible(true);
		field.set(null, null);

		OcpRuntimeException ocpRuntimeException =
				new OcpRuntimeException(null, null, null, null);

		Assert.assertNull(ocpRuntimeException.getMessage());

		// Reset server name to Test Server
		field.set(null, "Test Server");
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(null, null, null, null);
		Assert.assertEquals(null, ocpRuntimeException.getMessage());

	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(null, "Test Message", null, null, cause);
		Assert.assertEquals("Test Message", ocpRuntimeException.getMessage());

	}
}
