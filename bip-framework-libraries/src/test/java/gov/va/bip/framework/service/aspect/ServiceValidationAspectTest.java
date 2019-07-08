package gov.va.bip.framework.service.aspect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.messages.ServiceMessage;
import gov.va.bip.framework.service.DomainResponse;
import gov.va.bip.framework.service.aspect.ServiceValidationAspect;
import gov.va.bip.framework.service.aspect.validators.TestRequestValidator;
import gov.va.bip.framework.validation.Validator;

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
		} catch (Throwable e) {
			fail("Something went wrong");
		}

		assertNull(returned);
	}

	@Test
	public final void testCallPostValidationBasedOnDomainResponse() {
		try {
			DomainResponse domainResponse = mock(DomainResponse.class);
			when(domainResponse.hasErrors()).thenReturn(false);
			when(domainResponse.hasFatals()).thenReturn(false);
			ServiceValidationAspect aspect = mock(ServiceValidationAspect.class);
			Method method = null;
			try {
				method = this.getClass().getMethod("testMethod", String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				fail("exception not expected");
			}

			ReflectionTestUtils.invokeMethod(aspect, "callPostValidationBasedOnDomainResponse", proceedingJoinPoint, domainResponse,
					method);
		} catch (BipRuntimeException e) {
			assertTrue(e.getCause() instanceof NullPointerException);
		}
	}

	@Test
	public final void testAroundAdviceNoArgsHappy() {
		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		Signature mockSignature = mock(Signature.class);
		when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
		when(mockSignature.toShortString()).thenReturn("mock method signature");
		try {
			when(proceedingJoinPoint.proceed()).thenReturn(null);
		} catch (Throwable e1) {
			e1.printStackTrace();
			fail("exception not expected");
		}

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		} catch (Throwable e) {
			fail("Something went wrong");
		}

		assertNull(returned);
	}

	@Test(expected = BipRuntimeException.class)
	public final void testAddValidationErrorMessages_InstantiationExceptionCatchBlock() {
		Method mockMethod;
		try {
			mockMethod = this.getClass().getMethod("testMethodThrowingInstantiationException", String.class);
			ReflectionTestUtils.invokeMethod(aspect, "addValidationErrorMessages", mockMethod, null);
		} catch (NoSuchMethodException | SecurityException e) {
			fail("Unexpected exception");
		}
	}

	@Test(expected = BipRuntimeException.class)
	public final void testAddValidationErrorMessages_IllegalAccessExceptionCatchBlock() {
		Method mockMethod;
		try {
			mockMethod = this.getClass().getMethod("testMethodThrowingIllegalAccessException", String.class);
			ReflectionTestUtils.invokeMethod(aspect, "addValidationErrorMessages", mockMethod, null);
		} catch (NoSuchMethodException | SecurityException e) {
			fail("Unexpected exception");
		}
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
		} catch (Throwable e) {
			fail("Something went wrong");
		}

		assertNotNull(returned);
		assertNotNull(returned.getMessages());
		assertTrue(returned.getMessages().size() == 1);
		assertTrue(TestRequestValidator.SEVERITY.equals(
				returned.getMessages().get(0).getSeverity()));
		assertTrue(TestRequestValidator.KEY.getKey().equals(
				returned.getMessages().get(0).getKey()));
		assertTrue(TestRequestValidator.TEXT.equals(
				returned.getMessages().get(0).getText()));
		assertTrue(TestRequestValidator.STATUS.equals(
				returned.getMessages().get(0).getHttpStatus()));
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
		} catch (BipRuntimeException e) {
			assertTrue(e.getMessage().startsWith("Could not find or instantiate class "));
		}
	}

	@Test
	public final void testInvokeValidator() {
		try {
			ReflectionTestUtils.invokeMethod(aspect, "invokeValidator", new DomainResponse(), new LinkedList<ServiceMessage>(),
					this.getClass().getMethod("testMethod", String.class), DomainResponseValidatorForTest.class, new Object[] {});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unable to find method named testMethod");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unable to invoke method named testMethod");
		}
	}

	@Test(expected = BipRuntimeException.class)
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

	@Test
	public void testValidateInputsToTheMethodWithNullMethodParams() {
		List<Object> methodParams = null;
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
		assertNull(ReflectionTestUtils.invokeMethod(aspect, "validateInputsToTheMethod", methodParams, testMethod));

	}

	public void testMethod(final String testParam) {
		// do nothing
	}

	public DomainResponseThrowingInstantiationException testMethodThrowingInstantiationException(final String testParam) throws InstantiationException {
		return new DomainResponseThrowingInstantiationException();
	}

	public static class DomainResponseThrowingInstantiationException extends DomainResponse {

		public DomainResponseThrowingInstantiationException() throws InstantiationException {
			throw new InstantiationException();
		}
	}

	public DomainResponseThrowingIllegalAccessException testMethodThrowingIllegalAccessException(final String testParam)
			throws IllegalAccessException {
		return new DomainResponseThrowingIllegalAccessException();
	}

	public static class DomainResponseThrowingIllegalAccessException extends DomainResponse {

		public DomainResponseThrowingIllegalAccessException() throws IllegalAccessException {
			throw new IllegalAccessException();
		}
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
