package gov.va.bip.framework.rest.client.exception;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
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

import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.exception.BipPartnerException;
import gov.va.bip.framework.exception.BipPartnerRuntimeException;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.exception.BipRestGlobalExceptionHandler;

public class BipRestGlobalExceptionHandlerTest {

	BipRestGlobalExceptionHandler bipRestGlobalExceptionHandler = new BipRestGlobalExceptionHandler();

	DummyObjectToBeValidated dummyObjectToBeValidated;

	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@Test
	public void handleIllegalArgumentExceptionTest() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		IllegalArgumentException ex = new IllegalArgumentException("test illegal argument exception message");
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleIllegalArgumentException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
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
		objectErrors.add(oe);

		when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
		AuditLogSerializer serializer = new AuditLogSerializer();

		ReflectionTestUtils.setField(serializer, "dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		ReflectionTestUtils.setField(bipRestGlobalExceptionHandler, "asyncLogging", serializer);
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleMethodArgumentNotValidException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
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
		ResponseEntity<Object> response = bipRestGlobalExceptionHandler.handleHttpClientErrorException(req, ex);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
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

	static class DummyObjectToBeValidated {

		@NotBlank
		String dummyField;
	}

}
