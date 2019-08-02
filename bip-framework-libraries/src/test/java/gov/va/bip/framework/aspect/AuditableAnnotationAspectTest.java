package gov.va.bip.framework.aspect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ch.qos.logback.classic.spi.LoggingEvent;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.audit.annotation.Auditable;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.rest.provider.ProviderResponse;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuditableAnnotationAspectTest {

	private static final String TEST_STRING_ARGUMENTS = "Test_String1";

	private static final String TESTS_EXCEPTION_MESSAGE = "Test exception";

	private class TestMethodSignature implements org.aspectj.lang.reflect.MethodSignature {
		@Override
		public Class<?>[] getParameterTypes() {
			return new Class[] { String.class };
		}

		@Override
		public String[] getParameterNames() {
			return new String[] { "testParameter" };
		}

		@Override
		public Class<?>[] getExceptionTypes() {
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
		public Class<?> getDeclaringType() {
			return gov.va.bip.framework.aspect.AuditableAnnotationAspectTest.class;
		}

		@Override
		public String getDeclaringTypeName() {
			return "gov.va.bip.framework.rest.provider.aspect.AuditAnnotationAspectTest";
		}

		@Override
		public Class<?> getReturnType() {
			return String.class;
		}

		@Override
		public Method getMethod() {
			try {
				return AuditableAnnotationAspectTest.this.getClass().getMethod("annotatedMethod", new Class[] { String.class });
			} catch (NoSuchMethodException e) {
				fail("Error mocking the join point");
			} catch (SecurityException e) {
				fail("Error mocking the join point");
			}
			return null;
		}
	}

	@Mock
	ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	JoinPoint joinPoint;

	@Mock
	private ServletRequestAttributes attrs;

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;

	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	// added the mockAppender to the root logger
	@SuppressWarnings("unchecked")
	// It's not quite necessary but it also shows you how it can be done
	@Before
	public void setup() {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().addAppender(mockAppender);
	}

	// Always have this teardown otherwise we can stuff up our expectations.
	// Besides, it's
	// good coding practice
	@SuppressWarnings("unchecked")
	@After
	public void teardown() {
		BipLoggerFactory.getLogger(BipLogger.ROOT_LOGGER_NAME).getLoggerBoundImpl().detachAppender(mockAppender);
		SecurityContextHolder.clearContext();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuditAnnotationBefore() {
		when(joinPoint.getArgs()).thenReturn(new Object[] { TEST_STRING_ARGUMENTS });
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature());
		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			aspect.auditAnnotationBefore(joinPoint);
			verify(mockAppender, atLeastOnce()).doAppend(captorLoggingEvent.capture());
			final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
			assertNotNull(loggingEvents);
			assertTrue(loggingEvents.size() > 0);
			LoggingEvent event =
					loggingEvents.stream().filter(x -> x.getFormattedMessage().contains(TEST_STRING_ARGUMENTS)).findAny().orElse(null);
			assertNotNull(event);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuditAnnotationBeforeWithNoArgsJoinPoint() {
		when(joinPoint.getArgs()).thenReturn(new Object[] {});
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature() {
			@Override
			public Method getMethod() {
				try {
					return AuditableAnnotationAspectTest.this.getClass().getMethod("nonAnnotatedMethod", new Class[] { String.class });
				} catch (NoSuchMethodException e) {
					fail("Error mocking the join point");
				} catch (SecurityException e) {
					fail("Error mocking the join point");
				}
				return null;
			}
		});
		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			aspect.auditAnnotationBefore(joinPoint);
			verify(mockAppender, Mockito.times(3)).doAppend(captorLoggingEvent.capture());
			final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
			assertNotNull(loggingEvents);
			assertTrue(loggingEvents.size() > 0);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuditAnnotationAfterReturning() {
		when(joinPoint.getArgs()).thenReturn(new Object[] { TEST_STRING_ARGUMENTS });
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature());
		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			aspect.auditAnnotationAfterReturning(joinPoint, new ProviderResponse());
			verify(mockAppender, atLeastOnce()).doAppend(captorLoggingEvent.capture());
			final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
			assertNotNull(loggingEvents);
			LoggingEvent event =
					loggingEvents.stream().filter(x -> x.getFormattedMessage().contains("messages")).findAny().orElse(null);
			assertNotNull(event);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuditAnnotationAfterReturningWithNoAnnotation() {
		when(joinPoint.getArgs()).thenReturn(new Object[] { TEST_STRING_ARGUMENTS });
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature() {
			@Override
			public Method getMethod() {
				try {
					return AuditableAnnotationAspectTest.this.getClass().getMethod("nonAnnotatedMethod", new Class[] { String.class });
				} catch (NoSuchMethodException e) {
					fail("Error mocking the join point");
				} catch (SecurityException e) {
					fail("Error mocking the join point");
				}
				return null;
			}
		});
		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			aspect.auditAnnotationAfterReturning(joinPoint, new ProviderResponse());
			verify(mockAppender, Mockito.times(4)).doAppend(captorLoggingEvent.capture());
			final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
			assertNotNull(loggingEvents);
			assertTrue(loggingEvents.size() > 0);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAuditAnnotationAfterThrowing() {
		when(joinPoint.getArgs()).thenReturn(new Object[] { TEST_STRING_ARGUMENTS });
		when(joinPoint.getSignature()).thenReturn(new TestMethodSignature());
		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			try {
				aspect.auditAnnotationAfterThrowing(joinPoint, new Exception(TESTS_EXCEPTION_MESSAGE));
			} catch (Exception e) {
				// never mind this, the advice re-throws the exception passed in
			}
			verify(mockAppender, atLeastOnce()).doAppend(captorLoggingEvent.capture());
			final List<LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
			assertNotNull(loggingEvents);
			assertTrue(loggingEvents.size() > 0);
			LoggingEvent event = loggingEvents.stream()
					.filter(x -> x.getFormattedMessage().contains("An exception occurred in " + this.getClass().getName()))
					.filter(x -> x.getFormattedMessage().contains(TESTS_EXCEPTION_MESSAGE))
					.findAny()
					.orElse(null);
			assertNotNull(event);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}

	@Test
	public void testExceptionHandling() {

		when(joinPoint.getArgs()).thenThrow(IllegalStateException.class);
		when(joinPoint.getSignature()).thenThrow(IllegalStateException.class);

		RequestContextHolder.setRequestAttributes(attrs);
		AuditableAnnotationAspect aspect = new AuditableAnnotationAspect();
		AuditLogSerializer serializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", serializer);
		ReflectionTestUtils.setField(aspect, "baseAsyncAudit", baseAsyncAudit);
		try {
			aspect.auditAnnotationBefore(joinPoint);
			fail("Should have thrown exception on before");
		} catch (Throwable e) {
			assertTrue(IllegalStateException.class.equals(e.getCause().getClass()));
		}
		try {
			aspect.auditAnnotationAfterReturning(joinPoint, new ProviderResponse());
			fail("Should have thrown exception on afterReturning");
		} catch (Throwable e) {
			assertTrue(IllegalStateException.class.equals(e.getCause().getClass()));
		}
		try {
			aspect.auditAnnotationAfterThrowing(joinPoint, new Exception(TESTS_EXCEPTION_MESSAGE));
			fail("Should have thrown exception on afterThrowing");
		} catch (Throwable e) {
			assertTrue(IllegalStateException.class.equals(e.getCause().getClass()));
		}
	}

	@Auditable(event = AuditEvents.API_REST_REQUEST, activity = "testActivity")
	public void annotatedMethod(final String parameter) {

	}

	public void nonAnnotatedMethod(final String parameter) {

	}

}
