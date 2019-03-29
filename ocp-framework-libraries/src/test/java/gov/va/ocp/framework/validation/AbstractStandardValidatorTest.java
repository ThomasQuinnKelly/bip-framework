package gov.va.ocp.framework.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ocp.framework.messages.ServiceMessage;

public class AbstractStandardValidatorTest {

	private AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

		@Override
		public void validate(final String toValidate, final List<ServiceMessage> messages) {
			// do nothing
		}
	};

	@Test
	public void initializeAbstractStandardValidatorTest() {

		try {
			abstractStandardValidator.setCallingMethod(AbstractStandardValidatorTest.class.getMethod("testMethod", String.class));
		} catch (NoSuchMethodException e) {
			fail("unable to find testMethod");
		} catch (SecurityException e) {
			fail("unable to fetch testMethod");
		}
		List<ServiceMessage> messages = null;

		abstractStandardValidator.initValidate(null, messages, new Object());
		assertTrue(abstractStandardValidator.getValidatedType() == null);

		abstractStandardValidator.initValidate("test string object", messages, new Object());
		assertTrue(abstractStandardValidator.getValidatedType().equals(String.class));

		// sad path
		abstractStandardValidator.initValidate("test string object", messages, new Object());
		assertTrue(abstractStandardValidator.getValidatedType().equals(String.class));

		Method method = null;
		try {
			method = AbstractStandardValidatorTest.class.getMethod("testMethod", String.class);
			assertTrue(abstractStandardValidator.getCallingMethod().equals(method));
		} catch (NoSuchMethodException e) {
			fail("unable to find testMethod");
		} catch (SecurityException e) {
			fail("unable to fetch testMethod");
		}
		assertTrue(abstractStandardValidator.getCallingMethodName()
				.equals(method.getDeclaringClass().getSimpleName() + "." + method.getName() + ": "));
		assertTrue(abstractStandardValidator.hasSupplemental());
		assertTrue(abstractStandardValidator.hasSupplemental(Object.class));
		assertTrue(abstractStandardValidator.getSupplemental(Object.class) instanceof Object);
	}

	@Test
	public void setToValidateClassTest() {
		ReflectionTestUtils.invokeMethod(abstractStandardValidator, "setToValidateClass", "test Object");
		assertNotNull(abstractStandardValidator.getValidatedType());
		assertTrue(abstractStandardValidator.getValidatedType().equals(String.class));

		// sad path
		ReflectionTestUtils.invokeMethod(abstractStandardValidator, "setToValidateClass", new Object[] { null });
		assertNull(abstractStandardValidator.getValidatedType());
	}

	@Test
	public void handleInvalidClassTest() {
		AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

			@Override
			public void validate(final String toValidate, final List<ServiceMessage> messages) {
				// do nothing
			}
		};
		List<ServiceMessage> messages = new LinkedList<>();
		try {
			ReflectionTestUtils.setField(abstractStandardValidator, "toValidateClass", Integer.class);
			ReflectionTestUtils.invokeMethod(abstractStandardValidator, "handleInvalidClass", "test Object", messages);
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception not expected");
		}
	}

	@Test
	public void getSupplementalTest() {
		AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

			@Override
			public void validate(final String toValidate, final List<ServiceMessage> messages) {
				// do nothing
			}
		};
		ReflectionTestUtils.setField(abstractStandardValidator, "supplemental", new Object[] { "test object 1", "test object2" });
		Object[] returnValue = ReflectionTestUtils.invokeMethod(abstractStandardValidator, "getSupplemental");
		assertEquals(returnValue.length, 2);
	}

	@Test
	public void getSupplementalForGivenClassTest() {
		AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

			@Override
			public void validate(final String toValidate, final List<ServiceMessage> messages) {
				// do nothing
			}
		};
		ReflectionTestUtils.setField(abstractStandardValidator, "supplemental", new Object[] { "test object 1", "test object2" });
		Object returnValue = ReflectionTestUtils.invokeMethod(abstractStandardValidator, "getSupplemental", Integer.class);
		assertEquals(returnValue, null);
	}

	@Test
	public void hasSupplementalForGivenClassTest() {
		AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

			@Override
			public void validate(final String toValidate, final List<ServiceMessage> messages) {
				// do nothing
			}
		};
		ReflectionTestUtils.setField(abstractStandardValidator, "supplemental", new Object[] { "test object 1", "test object2" });
		Boolean returnValue = ReflectionTestUtils.invokeMethod(abstractStandardValidator, "hasSupplemental", Integer.class);
		assertFalse(returnValue);
	}

	public void testMethod(final String testParam) {
		System.out.println(testParam);
	}

}
