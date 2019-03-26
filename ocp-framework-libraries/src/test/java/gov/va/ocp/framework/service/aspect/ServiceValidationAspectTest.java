package gov.va.ocp.framework.service.aspect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.service.DomainResponse;
import gov.va.ocp.framework.service.aspect.validators.TestRequestValidator;
import gov.va.ocp.framework.validation.Validator;

@RunWith(MockitoJUnitRunner.class)
public class ServiceValidationAspectTest {

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	DomainResponseValidatorForTest validator;

	@Mock
	private MethodSignature signature;

	ServiceValidationAspect aspect = new ServiceValidationAspect();

	@Before
	public void setUp() throws Exception {
		assertNotNull(aspect);

		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
	}

	@After
	public void tearDown() throws Exception {
	}

	DomainResponse testMethodOneArg(final TestRequest test) {
		DomainResponse dr = null;
		return dr;
	}

	DomainResponse testMethodNoArg() {
		DomainResponse dr = null;
		return dr;
	}

	DomainResponse testMethodSad(final TestRequest test) {
		DomainResponse dr = null;
		return dr;
	}

	@Test
	public final void testAroundAdviceOneArgHappy() {
		Object[] args = new Object[1];
		args[0] = new TestRequest();

		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(args);
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		when(proceedingJoinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn("testMethodOneArg");
		when(signature.getDeclaringType()).thenReturn(this.getClass());

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNull(returned);
	}

	@Test
	public final void testAroundAdviceNoArgsHappy() {
		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNull(returned);
	}

	@Test
	public final void testAroundAdviceOneArgSad() {
		Object[] args = new Object[1];
		args[0] = new TestRequest();

		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(args);
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		when(proceedingJoinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn("testMethodSad");
		when(signature.getDeclaringType()).thenReturn(this.getClass());

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNotNull(returned);
		assertNotNull(returned.getMessages());
		assertTrue(returned.getMessages().size() == 1);
		assertTrue(TestRequestValidator.SEVERITY.equals(
				returned.getMessages().get(0).getSeverity()));
		assertTrue(TestRequestValidator.KEY.equals(
				returned.getMessages().get(0).getKey()));
		assertTrue(TestRequestValidator.TEXT.equals(
				returned.getMessages().get(0).getText()));
		assertTrue(TestRequestValidator.STATUS.equals(
				returned.getMessages().get(0).getHttpStatus()));
	}

	@Test
	public final void testAroundAdviceWhenExceptionIsThrown() {
		Object[] args = new Object[1];
		args[0] = new TestRequest();

		try {
			aspect.aroundAdvice(proceedingJoinPoint);
			fail("Should throw an exception");
		} catch (Exception e) {
			assertTrue(e instanceof OcpRuntimeException);
		}

	}

	@Test
	public final void testValidateResponse() {
		try {
			ReflectionTestUtils.invokeMethod(aspect, "validateResponse", new DomainResponse(),
					new LinkedList<ServiceMessage>(),
					this.getClass().getMethod("testMethod", String.class), new Object[] {});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unable to find method named testMethod");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unable to invoke method named testMethod");
		} catch (OcpRuntimeException e) {
			assertTrue(e.getMessage().startsWith("Could not find or instantiate class "));
		}
	}

	@Test(expected = OcpRuntimeException.class)
	public final void testHanldeExceptions() {
		ReflectionTestUtils.invokeMethod(aspect, "hanldeExceptions", Object.class, new IllegalAccessException(), new Object());
	}

	@Test(expected = OcpRuntimeException.class)
	public final void testHandleExceptionInAroundAspect() {
		ReflectionTestUtils.invokeMethod(aspect, "handleExceptionInAroundAspect", new OcpRuntimeException(null, null, null, null));
	}

	@Test(expected = OcpRuntimeException.class)
	public final void testValidateRequest() {
		LinkedList<ServiceMessage> messages = new LinkedList<ServiceMessage>();
		Method testMethod = null;
		try {
			testMethod = this.getClass().getMethod("testMethod", String.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unable to find method named testMethod");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unable to find method named testMethod");
		}
		ReflectionTestUtils.invokeMethod(aspect, "validateRequest", new DomainResponse(), messages, testMethod);
	}

	public void testMethod(final String testParam) {
		// do nothing
	}

	public static class DomainResponseValidatorForTest implements Validator<DomainResponse> {

		public DomainResponseValidatorForTest() {

		}

		@Override
		public void initValidate(final Object toValidate, final List<ServiceMessage> messages, final Object... supplemental) {
			// do nothing

		}

		@Override
		public void validate(final DomainResponse toValidate, final List<ServiceMessage> messages) {
			// do nothing

		}

		@Override
		public Class<DomainResponse> getValidatedType() {
			// do nothing
			return null;
		}

		@Override
		public void setCallingMethod(final Method callingMethod) {
			// do nothing

		}

		@Override
		public Method getCallingMethod() {
			// do nothing
			return null;
		}

	}

}
