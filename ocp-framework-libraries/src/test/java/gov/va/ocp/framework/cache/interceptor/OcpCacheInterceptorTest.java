package gov.va.ocp.framework.cache.interceptor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.service.DomainResponse;

@RunWith(MockitoJUnitRunner.class)
public class OcpCacheInterceptorTest {

	@Mock
	AuditLogSerializer asyncAuditLogSerializer = new AuditLogSerializer();

	OcpCacheInterceptor ocpCacheInterceptor = new OcpCacheInterceptor();

	class TestObject {
		public String testMethod(String msg) {
			return "Hello";
		}
	}

	class TestInvocation implements MethodInvocation {
		Method method;

		public TestInvocation() {
			try {
				method = TestObject.class.getMethod("testMethod", String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Could not set method");
			}
		}

		@Override
		public Object[] getArguments() {
			return new Object[] { "Hello" };
		}

		@Override
		public Object proceed() throws Throwable {
			return new DomainResponse();
		}

		@Override
		public Object getThis() {
			return this;
		}

		@Override
		public AccessibleObject getStaticPart() {
			return method;
		}

		@Override
		public Method getMethod() {
			return method;
		}
	}

	class BrokenTestInvocation extends TestInvocation {
		Method method;

		public BrokenTestInvocation() {
			super();
		}

		@Override
		public Method getMethod() {
			throw new RuntimeException("Testing");
		}
	}

	@Before
	public void setup() throws Throwable {
		doNothing().when(asyncAuditLogSerializer).asyncLogRequestResponseAspectAuditData(any(), any(), any(), any(), any());
		ocpCacheInterceptor.asyncLogging = asyncAuditLogSerializer;
	}

	@Test
	public final void testOcpCacheInterceptor() {
		assertNotNull(new OcpCacheInterceptor());
	}

	@Test
	public final void testInvokeMethodInvocation() throws Throwable {
		TestInvocation testInvocation = new TestInvocation();

		testInvocation = new TestInvocation();
		Object ret = ocpCacheInterceptor.invoke(testInvocation);
		assertNotNull(ret);
		assertTrue(DomainResponse.class.isAssignableFrom(ret.getClass()));
		assertTrue(((DomainResponse) ret).getMessages().isEmpty());
	}

	@Test (expected = Throwable.class)
	public final void testHandleInternalException() throws Throwable {
		BrokenTestInvocation testInvocation = new BrokenTestInvocation();

		ocpCacheInterceptor.invoke(testInvocation);
	}
}
