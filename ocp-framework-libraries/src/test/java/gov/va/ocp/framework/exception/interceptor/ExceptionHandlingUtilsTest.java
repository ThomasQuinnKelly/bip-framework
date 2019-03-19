package gov.va.ocp.framework.exception.interceptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.exception.interceptor.ExceptionHandlingUtils;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

@RunWith(SpringRunner.class)
public class ExceptionHandlingUtilsTest {

	private static final String TEST_MESSAGE = "test message";

	private static final String TEST_KEY = "test key";

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;

	@Mock
	NullPointerException nullPointerException;

	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	OcpLogger logger = OcpLoggerFactory.getLogger(ExceptionHandlingUtilsTest.class);

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		logger.setLevel(Level.DEBUG);
		logger.getLoggerBoundImpl().addAppender(mockAppender);
	}

	@After
	public void tearDown() {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLoggingUtils() throws Exception {
		// setup
		Object[] args = new Object[2];
		args[0] = "Arg One";
		args[1] = 42L;

		Level originalLevel = logger.getLevel();
		logger.setLevel(Level.WARN);

		ExceptionHandlingUtils.logException("Catcher", this.getClass().getMethod("testLoggingUtils"), args,
				new Throwable("test throw"));
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();
		Assert.assertTrue(loggingEvents.get(0).toString().startsWith(
				"[WARN] Catcher caught exception, handling it as configured.  Here are details [java.lang.Throwable thrown by gov.va.ocp.framework.exception.interceptor.ExceptionHandlingUtilsTest.testLoggingUtils] args [[Arg One, 42]]."));
		Assert.assertEquals("java.lang.Throwable", loggingEvents.get(0).getThrowableProxy().getClassName());
		Assert.assertEquals(ch.qos.logback.classic.Level.WARN, loggingEvents.get(0).getLevel());

		logger.setLevel(originalLevel);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLoggingWarnOff() throws Exception {
		final OcpLogger logger = OcpLoggerFactory.getLogger(ExceptionHandlingUtilsTest.class);
		Level originalLevel = logger.getLevel();
		logger.setLevel(Level.ERROR);

		ExceptionHandlingUtils.logException("Catcher", myMethod(), null, new Throwable("test throw"));
		verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
		final List<ch.qos.logback.classic.spi.LoggingEvent> loggingEvents = captorLoggingEvent.getAllValues();

		Assert.assertEquals(
				"[ERROR] Catcher caught exception, handling it as configured.  Here are details [java.lang.Throwable thrown by gov.va.ocp.framework.exception.interceptor.ExceptionHandlingUtilsTest.someMethod] args [null].",
				loggingEvents.get(0).toString());
		Assert.assertEquals(ch.qos.logback.classic.Level.ERROR, loggingEvents.get(0).getLevel());

		logger.setLevel(originalLevel);
	}

	@Test
	public void testResolveRuntimeExceptionWithClassCastException() {
		ExceptionHandlingUtils.resolveRuntimeException(nullPointerException);
	}

	@Test
	public void testConvertFromOcpExceptionExtender() {
		OcpRuntimeException resolvedRuntimeException = ExceptionHandlingUtils.convertFromOcpExceptionExtender(
				new OcpRuntimeException(TEST_KEY, TEST_MESSAGE, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertTrue(resolvedRuntimeException.getKey().equals(TEST_KEY));
		assertTrue(resolvedRuntimeException.getMessage().equals(TEST_MESSAGE));
		assertTrue(resolvedRuntimeException.getSeverity().equals(MessageSeverity.ERROR));
		assertTrue(resolvedRuntimeException.getStatus().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void testConvertFromOcpExceptionExtenderWithClassCastException() {
		try {
			ExceptionHandlingUtils.convertFromOcpExceptionExtender(new RuntimeException());
		} catch (OcpRuntimeException e) {
			assertTrue(e.getKey().equals(""));
			String msg = "Could not instantiate OcpRuntimeException using values from throwable java.lang.RuntimeException";
			assertTrue(e.getMessage().equals(msg));
			assertTrue(e.getSeverity().equals(MessageSeverity.FATAL));
			assertTrue(e.getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@Test
	public void testCastToOcpRuntimeException() {
		try {
			ExceptionHandlingUtils.castToOcpRuntimeException(
					new OcpRuntimeException(TEST_KEY, TEST_MESSAGE, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		} catch (OcpRuntimeException e) {
			assertTrue(e.getKey().equals(""));
			String msg = "Could not instantiate OcpRuntimeException using values from throwable java.lang.RuntimeException";
			assertTrue(e.getMessage().equals(msg));
			assertTrue(e.getSeverity().equals(MessageSeverity.FATAL));
			assertTrue(e.getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@Test
	public void testCastToOcpRuntimeExceptionExtenderWithClassCastException() {
		try {
			ExceptionHandlingUtils.convertFromOcpExceptionExtender(new RuntimeException());
		} catch (OcpRuntimeException e) {
			assertTrue(e.getKey().equals(""));
			String msg = "Could not instantiate OcpRuntimeException using values from throwable java.lang.RuntimeException";
			assertTrue(e.getMessage().equals(msg));
			assertTrue(e.getSeverity().equals(MessageSeverity.FATAL));
			assertTrue(e.getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}

}
