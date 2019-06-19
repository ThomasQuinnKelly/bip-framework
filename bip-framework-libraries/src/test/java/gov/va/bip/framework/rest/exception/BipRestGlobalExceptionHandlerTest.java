package gov.va.bip.framework.rest.exception;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import gov.va.bip.framework.AbstractBaseLogTester;
import gov.va.bip.framework.exception.BipExceptionData;
import gov.va.bip.framework.exception.BipPartnerException;
import gov.va.bip.framework.exception.BipPartnerRuntimeException;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.shared.sanitize.SanitizerException;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class BipRestGlobalExceptionHandlerTest extends AbstractBaseLogTester {

	BipRestGlobalExceptionHandler bipRestGlobalExceptionHandler = new BipRestGlobalExceptionHandler();

	DummyObjectToBeValidated dummyObjectToBeValidated;

	private static final String[] params = new String[] {};
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;
	private static final String TEST_KEY_TEXT = "NO_KEY";
	private static final String TEST_MESSAGE = "Test message";

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;

	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	// added the mockAppender to the root logger
	@Override
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

	@Test
	public void handleIllegalArgumentExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		IllegalArgumentException ex = new IllegalArgumentException("test illegal argument exception message");
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleIllegalArgumentException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleIllegalStateExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		IllegalStateException ex = new IllegalStateException("test illegal state exception message");
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleIllegalStateException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logSeverityNullTest()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method logMethod = bipRestGlobalExceptionHandler.getClass().getDeclaredMethod("log", Exception.class, MessageKey.class,
				MessageSeverity.class, HttpStatus.class, String[].class);
		logMethod.setAccessible(true);

		/* debug, null severity, status */
		logMethod.invoke(bipRestGlobalExceptionHandler,
				new Exception(TEST_MESSAGE), TEST_KEY, (MessageSeverity) null, HttpStatus.ACCEPTED, params);
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		ch.qos.logback.classic.spi.LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		assertTrue("INFO".equals(loggingEvent.getLevel().toString()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logInfoTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException {
		Method logMethod = bipRestGlobalExceptionHandler.getClass().getDeclaredMethod("log", Exception.class, MessageKey.class,
				MessageSeverity.class, HttpStatus.class, String[].class);
		logMethod.setAccessible(true);

		/* info, severity, status */
		logMethod.invoke(bipRestGlobalExceptionHandler,
				new Exception(TEST_MESSAGE), TEST_KEY, MessageSeverity.INFO, HttpStatus.ACCEPTED, params);
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		ch.qos.logback.classic.spi.LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		assertTrue("INFO".equals(loggingEvent.getLevel().toString()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logDebugTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException {
		Method logMethod = bipRestGlobalExceptionHandler.getClass().getDeclaredMethod("log", Exception.class, MessageKey.class,
				MessageSeverity.class, HttpStatus.class, String[].class);
		logMethod.setAccessible(true);

		/* info, severity, status */
		logMethod.invoke(bipRestGlobalExceptionHandler,
				new Exception(TEST_MESSAGE), TEST_KEY, MessageSeverity.DEBUG, HttpStatus.ACCEPTED, params);
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		ch.qos.logback.classic.spi.LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		assertTrue("DEBUG".equals(loggingEvent.getLevel().toString()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logWarnTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException {
		Method logMethod = bipRestGlobalExceptionHandler.getClass().getDeclaredMethod("log", Exception.class, MessageKey.class,
				MessageSeverity.class, HttpStatus.class, String[].class);
		logMethod.setAccessible(true);

		/* warn, severity, status */
		logMethod.invoke(bipRestGlobalExceptionHandler,
				new Exception(TEST_MESSAGE), TEST_KEY, MessageSeverity.WARN, HttpStatus.ACCEPTED, params);
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		ch.qos.logback.classic.spi.LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		assertTrue("WARN".equals(loggingEvent.getLevel().toString()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logErrorNoStatusTest()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method logMethod = bipRestGlobalExceptionHandler.getClass().getDeclaredMethod("log", Exception.class, MessageKey.class,
				MessageSeverity.class, HttpStatus.class, String[].class);
		logMethod.setAccessible(true);

		/* error, severity, no status */
		logMethod.invoke(bipRestGlobalExceptionHandler,
				new Exception(TEST_MESSAGE), TEST_KEY, MessageSeverity.ERROR, HttpStatus.ACCEPTED, params);
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		ch.qos.logback.classic.spi.LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		assertTrue("ERROR".equals(loggingEvent.getLevel().toString()));
	}

	@Test
	public void deriveMessageTests() {
		// null exception
		String returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", (Exception) null);
		assertTrue(returnValue.contains(BipRestGlobalExceptionHandler.NO_EXCEPTION_MESSAGE));

		// exception without cause
		returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage",
				new BipRuntimeException(TEST_KEY, MessageSeverity.DEBUG, HttpStatus.BAD_REQUEST));
		assertTrue(returnValue.equals(TEST_KEY_TEXT));

		// exception without cause or message
		returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", new Exception());
		assertTrue(returnValue.contains(BipRestGlobalExceptionHandler.NO_EXCEPTION_MESSAGE));

		// exception with message; cause that has a message
		Exception cause = new IllegalStateException(TEST_MESSAGE);
		returnValue =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", new Exception(TEST_MESSAGE, cause));
		assertTrue(returnValue.contains(TEST_MESSAGE));

		// exception with blank space message; cause that has a message
		cause = new IllegalStateException(TEST_MESSAGE);
		returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", new Exception("  ", cause));
		assertTrue(returnValue.contains(BipRestGlobalExceptionHandler.NO_EXCEPTION_MESSAGE));

		// exception without message; cause that has a message
		cause = new IllegalStateException(TEST_MESSAGE);
		returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", new Exception(cause));
		assertTrue(returnValue.contains(TEST_MESSAGE));

		// exception without message; cause that does not have a message
		cause = new IllegalStateException("");
		returnValue = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "deriveMessage", new Exception(cause));
		assertTrue(returnValue.contains(BipRestGlobalExceptionHandler.NO_EXCEPTION_MESSAGE));
	}

	@Test
	public void handleMethodArgumentNotValidExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		MethodParameter parameter = null;

		try {
			parameter = new MethodParameter(BipRestGlobalExceptionHandlerTest.this.getClass()
					.getMethod("methodForExtractingMethodObject", new Class[] { String.class }), 0);
		} catch (NoSuchMethodException e) {
			fail("Error mocking the parameter");
		} catch (SecurityException e) {
			fail("Error mocking the parameter");
		}

		BindingResult bindingResult = mock(BindingResult.class);
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

		List<FieldError> fieldErrors = new LinkedList<>();
		FieldError fe = new FieldError("test object name", "test field", "test rejected value", true, new String[] { "test code" },
				new Object[] { "test argument" }, "test default message");
		fieldErrors.add(fe);

		when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

		List<ObjectError> objectErrors = new LinkedList<>();
		ObjectError oe = new ObjectError("test ObjectError objectName", "test ObjectError DefaultMessage");
		ReflectionTestUtils.setField(oe, "codes", new String[] { "code1", "code2" });
		objectErrors.add(oe);

		when(bindingResult.getGlobalErrors()).thenReturn(objectErrors);
		AnnotationConfigApplicationContext annotationConfigApplicationContext =
				new AnnotationConfigApplicationContext(TestConfigurationForAuditHttpServletResponseBean.class);
		BipRestGlobalExceptionHandler brgeh = annotationConfigApplicationContext.getBean(BipRestGlobalExceptionHandler.class);

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

		/* Test with request and exception */
		ResponseEntity<Object> response = brgeh.handleMethodArgumentNotValidException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		annotationConfigApplicationContext.close();

		/* Test with request and no exception */
		response = brgeh.handleMethodArgumentNotValidException(req, (MethodArgumentNotValidException) null);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
		annotationConfigApplicationContext.close();
	}

	public void methodForExtractingMethodObject(final String parameter) {

	}

	@Test
	public void handleHttpClientErrorExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "test status text", headers,
				"test body".getBytes(), Charset.defaultCharset());

		/* With request and exception */
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleHttpClientErrorException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

		/* With request and no exception */
		response = bipRestGlobalExceptionHandler.handleHttpClientErrorException(req, (HttpClientErrorException) null);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void handleMethodArgumentTypeMismatchTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		MethodParameter parameter = null;
		try {
			parameter = new MethodParameter(BipRestGlobalExceptionHandlerTest.this.getClass()
					.getMethod("methodForExtractingMethodObject", new Class[] { String.class }), 0);
		} catch (NoSuchMethodException e) {
			fail("Error mocking the parameter");
		} catch (SecurityException e) {
			fail("Error mocking the parameter");
		}
		MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("test value", String.class, "test name",
				parameter, new Exception("test wrapped message"));
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleMethodArgumentTypeMismatch(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleConstraintViolationTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		BipRestGlobalExceptionHandlerTest.DummyObjectToBeValidated dummyObject =
				new BipRestGlobalExceptionHandlerTest.DummyObjectToBeValidated();
		dummyObject.dummyField = "";

		Set<? extends ConstraintViolation<?>> constaintViolations = validator.validate(dummyObject, Default.class);

		ConstraintViolationException ex = new ConstraintViolationException("test message", constaintViolations);
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleConstraintViolation(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleConstraintViolationWithNullArgumentForExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		BipRestGlobalExceptionHandlerTest.DummyObjectToBeValidated dummyObject =
				new BipRestGlobalExceptionHandlerTest.DummyObjectToBeValidated();
		dummyObject.dummyField = "";

		Set<? extends ConstraintViolation<?>> constaintViolations = validator.validate(dummyObject, Default.class);

		new ConstraintViolationException("test message", constaintViolations);
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleConstraintViolation(req, null);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void handleHttpMessageNotReadableExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);

		HttpMessageNotReadableException ex =
				new HttpMessageNotReadableException("test msg", new Exception("wrapped message"), new HttpInputMessage() {

					@Override
					public HttpHeaders getHeaders() {
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.TEXT_PLAIN);
						return headers;
					}

					@Override
					public InputStream getBody() throws IOException {
						return new ByteArrayInputStream("test body".getBytes());
					}
				});

		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleHttpMessageNotReadableException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleNoHandlerFoundExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		NoHandlerFoundException ex = new NoHandlerFoundException("test msg", "wrapped message", headers);

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleNoHandlerFoundException", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));
	}

	@Test
	public void handleHttpRequestMethodNotSupportedTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		List<String> suppotedMethods = Arrays.asList(new String[] { "GET" });
		HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("test method name", suppotedMethods);

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleHttpRequestMethodNotSupported", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.METHOD_NOT_ALLOWED));
	}

	@Test
	public void handleHttpMediaTypeNotSupportedTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		Arrays.asList(new String[] { "supported method1" });
		List<MediaType> supportedMediatypes = new LinkedList<>();
		supportedMediatypes.add(MediaType.TEXT_HTML);
		HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, supportedMediatypes);

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleHttpMediaTypeNotSupported", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
	}

	@Test
	public void handleSanitizerExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		SanitizerException ex = new SanitizerException("test", new Exception("test exception"));

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleSanitizerException", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleBipRuntimeExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		BipRuntimeException ex = new BipRuntimeException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleBipRuntimeException", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleAllTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		Exception ex = new Exception("test message");

		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleAll(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void failSafeHandlerTest() {
		ResponseEntity<Object> response = ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "failSafeHandler");
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void standardHandlerWithNullExceptionTest() {
		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "standardHandler", null, HttpStatus.BAD_REQUEST);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void standardHandlerWithNullMessagekeyTest()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		BipRuntimeException ex = new BipRuntimeException(MessageKeys.NO_KEY, MessageSeverity.DEBUG, HttpStatus.BAD_REQUEST);
		Field exceptionData = ex.getClass().getDeclaredField("exceptionData");
		exceptionData.setAccessible(true);
		exceptionData.set(ex, new BipExceptionData((MessageKey) null, ((BipExceptionData) exceptionData.get(ex)).getSeverity(),
				((BipExceptionData) exceptionData.get(ex)).getStatus(), ((BipExceptionData) exceptionData.get(ex)).getParams()));

		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "standardHandler", ex, HttpStatus.BAD_REQUEST);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void handleBipPartnerRuntimeExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		BipPartnerRuntimeException ex =
				new BipPartnerRuntimeException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleBipPartnerRuntimeException", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void handleBipPartnerCheckedExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		BipPartnerException ex = new BipPartnerException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "handleBipPartnerCheckedException", req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void standardHandlerWithWarnTest() {
		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "standardHandler", new Exception(),
						TEST_KEY, MessageSeverity.WARN, HttpStatus.OK, null);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
	}

	@Test
	public void standardHandlerWithNullException2Test() {
		ResponseEntity<Object> response =
				ReflectionTestUtils.invokeMethod(bipRestGlobalExceptionHandler, "standardHandler", null,
						TEST_KEY, MessageSeverity.WARN, HttpStatus.OK, null);
		assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	static class DummyObjectToBeValidated {

		@NotBlank
		String dummyField;
	}

	@Configuration
	@ComponentScan(basePackages = { "gov.va.bip.framework" })
	static class TestConfigurationForAuditHttpServletResponseBean {

	}

}
