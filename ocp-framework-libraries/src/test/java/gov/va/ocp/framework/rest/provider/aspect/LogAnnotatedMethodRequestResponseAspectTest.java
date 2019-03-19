package gov.va.ocp.framework.rest.provider.aspect;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.Auditable;

@RunWith(SpringJUnit4ClassRunner.class)
public class LogAnnotatedMethodRequestResponseAspectTest {

	private static final String TEST_RETURN_VALUE = "testReturnValue";

	private Method method;

	private static final String TEST_STRING_ARGUMENTS = "Test_String1";

	private final class TestMethodSignature implements org.aspectj.lang.reflect.MethodSignature {
		@Override
		public Class[] getParameterTypes() {
			return new Class[] { String.class };
		}

		@Override
		public String[] getParameterNames() {
			return new String[] { "testParameter" };
		}

		@Override
		public Class[] getExceptionTypes() {
			return null;
		}

		@Override
		public String toShortString() {
			return "testMethodSignatureShort";
		}

		@Override
		public String toLongString() {
			return "testMethodSignatureLong";
		}

		@Override
		public String getName() {
			return "testMethod";
		}

		@Override
		public int getModifiers() {
			return 0;
		}

		@Override
		public Class getDeclaringType() {
			return gov.va.ocp.framework.rest.provider.aspect.LogAnnotatedMethodRequestResponseAspectTest.class;
		}

		@Override
		public String getDeclaringTypeName() {
			return "gov.va.ocp.framework.rest.provider.aspect.LogAnnotatedMethodRequestResponseAspectTest";
		}

		@Override
		public Class getReturnType() {
			// TODO Auto-generated method stub
			return String.class;
		}

		@Override
		public Method getMethod() {
			try {
				return LogAnnotatedMethodRequestResponseAspectTest.this.getClass().getMethod("annotatedMethod", new Class[] {String.class});
			} catch (NoSuchMethodException e) {
				fail("Error mocking the join point");
			} catch (SecurityException e) {
				fail("Error mocking the join point");
			}
			return null;
		}
	}

	@Mock
	ProceedingJoinPoint joinPoint;

	@Mock
	private ServletRequestAttributes attrs;

	@Test
	public void testLogAnnotatedMethodRequestResponse() {
		when(joinPoint.getArgs()).thenReturn(new Object[] { TEST_STRING_ARGUMENTS });
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature());
		try {
			when(joinPoint.proceed()).thenReturn(TEST_RETURN_VALUE);
		} catch (Throwable e) {
			fail("Unable to mock joinPoint");
		}
		RequestContextHolder.setRequestAttributes(attrs);
		LogAnnotatedMethodRequestResponseAspect aspect = new LogAnnotatedMethodRequestResponseAspect();
		Object returnValue = null;
		try {
			returnValue = aspect.logAnnotatedMethodRequestResponse(joinPoint);
			assertTrue(returnValue.equals(TEST_RETURN_VALUE));
		} catch (Throwable e) {
			fail("Exception should not be thrown");
		}
	}

	@Auditable(event = AuditEvents.REQUEST_RESPONSE, activity = "testActivity")
	public void annotatedMethod(final String parameter) {

	}

}
