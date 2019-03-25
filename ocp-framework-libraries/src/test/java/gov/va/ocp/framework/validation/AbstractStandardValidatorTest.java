package gov.va.ocp.framework.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ocp.framework.messages.ServiceMessage;

public class AbstractStandardValidatorTest {

	@Test
	public void initializeAbstractStandardValidatorTest() {

		AbstractStandardValidator<String> abstractStandardValidator = new AbstractStandardValidator<String>() {

			@Override
			public void validate(final String toValidate, final List<ServiceMessage> messages) {
				// do nothing
			}
		};

		try {
			abstractStandardValidator.setCallingMethod(AbstractStandardValidatorTest.class.getMethod("testMethod", String.class));
		} catch (NoSuchMethodException e) {
			fail("unable to find testMethod");
		} catch (SecurityException e) {
			fail("unable to fetch testMethod");
		}
		List<ServiceMessage> messages = null;
		abstractStandardValidator.initValidate(null, messages, new Object());
		ReflectionTestUtils.setField(abstractStandardValidator, "toValidateClass", String.class);
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

	public void testMethod(final String testParam) {
		System.out.println(testParam);
	}

}
