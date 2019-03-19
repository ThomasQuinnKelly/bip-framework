package gov.va.ocp.framework.rest.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MediaTypeNotSupportedStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpPartnerException;
import gov.va.ocp.framework.exception.OcpPartnerRuntimeException;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;

/**
 * A global exception handler as the last line of defense before sending response to the service consumer.
 * This class converts exceptions to appropriate {@link gov.va.ocp.framework.rest.provider.Message} objects and puts them on the
 * response.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OcpRestGlobalExceptionHandler {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(OcpRestGlobalExceptionHandler.class);

	/**
	 * Return value if no key has been specified.
	 * To get default key, use {@link #deriveKey(OcpExceptionExtender)} or {@link #deriveKey(String)}.
	 */
	private static final String NO_KEY = "NO-KEY";

	/**
	 * Return value if no exception exists to provide a message.
	 * To get default message text, use {@link #deriveMessage(Exception)}.
	 */
	private static final String NO_MESSAGE = "Source exception has no message.";

	/**
	 * For java.lang.Exception and all subclasses.
	 * If exception message is empty, gets message of the cause if it exists.
	 *
	 * @param ex the Exception
	 * @return String the message
	 */
	private String deriveMessage(Exception ex) {
		return ex == null ? NO_MESSAGE
				: StringUtils.isBlank(ex.getMessage()) && ex.getCause() != null
				? ex.getCause().getMessage()
						: StringUtils.isBlank(ex.getMessage()) ? NO_MESSAGE : ex.getMessage();
	}

	/**
	 * For any exception that implements {@link OcpExceptionExtender}.
	 * If key is empty, returns the constant "NO-KEY".
	 *
	 * @param ex the exception that implements OcpExceptionExtender
	 * @return String the key, or NO_KEY
	 */
	private String deriveKey(OcpExceptionExtender ex) {
		return deriveKey(ex == null || StringUtils.isBlank(ex.getKey()) ? NO_KEY : ex.getKey());
	}

	/**
	 * If key is empty, returns the constant "NO-KEY".
	 *
	 * @param key the initial string intended to represent the key
	 * @return String the key, or NO_KEY
	 */
	private String deriveKey(String key) {
		return StringUtils.isBlank(key) ? NO_KEY : key;
	}

	/**
	 * INFO logs the exception and its details.
	 *
	 * @param ex the exception
	 * @param key the key to use for reporting to support/maintenance
	 * @param severity the MessageSeverity to report for the exception
	 * @param status the status to report for the exception
	 */
	private void log(Exception ex, String key, MessageSeverity severity, HttpStatus status) {
		log(Level.INFO, ex, key, deriveMessage(ex), severity, status);
	}

	/**
	 * Logs the exception and its details.
	 *
	 * @param level the Log Level to log at
	 * @param ex the exception
	 * @param key the key to use for reporting to support/maintenance
	 * @param message the message
	 * @param severity the MessageSeverity to report for the exception
	 * @param status the status to report for the exception
	 */
	private void log(Level level, Exception ex, String key, String message, MessageSeverity severity, HttpStatus status) {
		String msg = status + "-" + severity + " "
				+ (ex == null ? "null" : ex.getClass().getName()) + " "
				+ deriveKey(key) + ":" + message;
		if (Level.ERROR.equals(level)) {
			logger.error(msg, ex);
		} else if (Level.WARN.equals(level)) {
			logger.warn(msg, ex);
		} else if (Level.INFO.equals(level)) {
			logger.info(msg, ex);
		} else {
			logger.debug(msg, ex);
		}
	}

	/**
	 * A last resort to return a (somewhat) meaningful response to the consumer when there is no source exception.
	 *
	 * @return ResponseEntity the HTTP Response Entity
	 */
	protected ResponseEntity<Object> failSafeHandler() {
		log(Level.ERROR, null, NO_KEY, NO_MESSAGE, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.FATAL, NO_KEY, NO_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Standard exception handler for any exception that implements {@link OcpExceptionExtender}.
	 *
	 * @param ex the exception that implements OcpExceptionExtender
	 * @param httpResponseStatus the status to put on the HTTP Response Entity.
	 * @return ResponseEntity the HTTP Response Entity
	 */
	protected ResponseEntity<Object> standardHandler(OcpExceptionExtender ex, HttpStatus httpResponseStatus) {
		if (ex == null) {
			return failSafeHandler();
		}
		return standardHandler((Exception) ex, deriveKey(ex), ex.getSeverity(), ex.getStatus(), httpResponseStatus);
	}

	/**
	 * Standard exception handler for any Exception.
	 *
	 * @param ex the Exception
	 * @param key the key to use for reporting to support/maintenance
	 * @param severity the MessageSeverity to report for the exception
	 * @param status the status to report for the exception
	 * @param httpResponseStatus the status to put on the HTTP Response Entity.
	 * @return ResponseEntity the HTTP Response Entity
	 */
	protected ResponseEntity<Object> standardHandler(Exception ex, String key, MessageSeverity severity, HttpStatus status,
			HttpStatus httpResponseStatus) {
		if (ex == null) {
			return failSafeHandler();
		}
		ProviderResponse apiError = new ProviderResponse();

		log(ex, key, severity, httpResponseStatus);
		apiError.addMessage(severity, deriveKey(key), deriveMessage(ex), httpResponseStatus);

		return new ResponseEntity<>(apiError, httpResponseStatus);
	}

	// 400

	/**
	 * Handle OcpPartnerRuntimeException.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpPartnerRuntimeException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleOcpPartnerRuntimeException(HttpServletRequest req, OcpPartnerRuntimeException ex) {
		return standardHandler(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle OcpPartnerException.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpPartnerException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleOcpPartnerCheckedException(HttpServletRequest req, OcpPartnerException ex) {
		return standardHandler(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle illegal argument exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleIllegalArgumentException(HttpServletRequest req, IllegalArgumentException ex) {
		return standardHandler(ex, "", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument not valid exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleMethodArgumentNotValidException(HttpServletRequest req,
			MethodArgumentNotValidException ex) {
		log(ex, "", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);

		final ProviderResponse apiError = new ProviderResponse();
		if (ex == null || ex.getBindingResult() == null) {
			return failSafeHandler();
		} else {
			for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
				apiError.addMessage(MessageSeverity.ERROR, error.getCodes()[0],
						error.getDefaultMessage(),
						HttpStatus.BAD_REQUEST);
			}
			for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
				apiError.addMessage(MessageSeverity.ERROR, error.getCodes()[0],
						error.getDefaultMessage(),
						HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle http client error exception.
	 *
	 * @param req the req
	 * @param httpClientErrorException the http client error exception
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpClientErrorException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleHttpClientErrorException(HttpServletRequest req,
			final HttpClientErrorException httpClientErrorException) {
		log(httpClientErrorException, "", null, null);

		ProviderResponse apiError = new ProviderResponse();
		if (httpClientErrorException == null) {
			return failSafeHandler();
		} else {
			byte[] responseBody = httpClientErrorException.getResponseBodyAsByteArray();

			try {
				apiError = new ObjectMapper().readValue(responseBody, ProviderResponse.class);
			} catch (IOException e) {
				log(e,"", MessageSeverity.ERROR, null);
				apiError.addMessage(MessageSeverity.ERROR, httpClientErrorException.getStatusCode().name(),
						new String(responseBody),
						httpClientErrorException.getStatusCode());
			}
		}

		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument type mismatch.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleMethodArgumentTypeMismatch(HttpServletRequest req,
			final MethodArgumentTypeMismatchException ex) {

		final String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();
		log(Level.INFO, ex, "", message, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST.name(),
				message, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle constraint violation.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleConstraintViolation(HttpServletRequest req, final ConstraintViolationException ex) {
		log(ex, "", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		final ProviderResponse apiError = new ProviderResponse();
		if (ex == null || ex.getConstraintViolations() == null) {
			return failSafeHandler();
		} else {
			for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
				apiError.addMessage(MessageSeverity.ERROR, violation.getRootBeanClass().getName() + " " + violation.getPropertyPath(),
						violation.getMessage(),
						HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle http message not readable exception.
	 *
	 * @param req the req
	 * @param httpMessageNotReadableException the http message not readable exception
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleHttpMessageNotReadableException(HttpServletRequest req,
			final HttpMessageNotReadableException httpMessageNotReadableException) {
		return standardHandler(httpMessageNotReadableException, "", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
				HttpStatus.BAD_REQUEST);
	}

	// 404

	/**
	 * Handle no handler found exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public final ResponseEntity<Object> handleNoHandlerFoundException(HttpServletRequest req, final NoHandlerFoundException ex) {
		return standardHandler(ex, "", MessageSeverity.ERROR, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	// 405

	/**
	 * Handle http request method not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public final ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpServletRequest req,
			final HttpRequestMethodNotSupportedException ex) {
		return standardHandler(ex, "", MessageSeverity.ERROR, HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
	}

	// 415

	/**
	 * Handle http media type not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MediaTypeNotSupportedStatusException.class)
	@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public final ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpServletRequest req,
			final HttpMediaTypeNotSupportedException ex) {
		return standardHandler(ex, "", MessageSeverity.ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	// Handle all

	/**
	 * Handle ocp runtime exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpRuntimeException.class)
	public final ResponseEntity<Object> handleOcpRuntimeException(HttpServletRequest req, OcpRuntimeException ex) {
		return standardHandler(ex, ex.getStatus());
	}

	/**
	 * Handle all.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<Object> handleAll(HttpServletRequest req, final Exception ex) {
		return standardHandler(ex, "", MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
