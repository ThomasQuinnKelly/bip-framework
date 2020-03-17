package gov.va.bip.framework.cache.interceptor;

import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.service.DomainResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class BipCacheInterceptorTest {

	@Mock
	AuditLogSerializer asyncAuditLogSerializer = new AuditLogSerializer();

	BipCacheInterceptor bipCacheInterceptor = new BipCacheInterceptor();

	class TestObject {
		public String testMethod(final String msg) {
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
		doNothing().when(asyncAuditLogSerializer).asyncAuditRequestResponseData(any(), any(), any(), any(), any());
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", asyncAuditLogSerializer);
		bipCacheInterceptor.baseAsyncAudit = baseAsyncAudit;
	}

	@Test
	public final void testBipCacheInterceptor() {
		assertNotNull(new BipCacheInterceptor());
	}

	@Test
	public final void testInvokeMethodInvocation() throws Throwable {
		TestInvocation testInvocation = new TestInvocation();

		testInvocation = new TestInvocation();
		Object ret = bipCacheInterceptor.invoke(testInvocation);
		assertNotNull(ret);
		assertTrue(DomainResponse.class.isAssignableFrom(ret.getClass()));
		assertTrue(((DomainResponse) ret).getMessages().isEmpty());
	}

	@Test(expected = Throwable.class)
	public final void testHandleInternalException() throws Throwable {
		BrokenTestInvocation testInvocation = new BrokenTestInvocation();

		bipCacheInterceptor.invoke(testInvocation);
	}

	@Test
	public final void testInvokeMethodInvocationWithDebugDisabled() throws Throwable {
		TestInvocation testInvocation = new TestInvocation();

		testInvocation = new TestInvocation();
		BipLogger logger = (BipLogger) ReflectionTestUtils.getField(bipCacheInterceptor, "LOGGER");
		logger.setLevel(Level.INFO);
		Object ret = bipCacheInterceptor.invoke(testInvocation);
		assertNotNull(ret);
		assertTrue(DomainResponse.class.isAssignableFrom(ret.getClass()));
		assertTrue(((DomainResponse) ret).getMessages().isEmpty());
		logger.setLevel(Level.DEBUG);
	}
}
