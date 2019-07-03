package gov.va.bip.framework.rest.exception;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.ProviderResponse;
import gov.va.bip.framework.util.HttpHeadersUtil;

/**
 * Spring Boot automatically registers a BasicErrorController bean if you don’t
 * specify any custom implementation in the configuration. However, this default
 * controller needs to be configured for BIP platform. By default Spring Boot
 * maps /error to BasicErrorController which populates model with error
 * attributes and then returns 'error' as the view name to map application
 * defined error pages. To replace BasicErrorController with our own custom
 * controller which can map to '/error', we need to implement ErrorController
 * interface. Also @ApiIgnore added to ignored controller method parameter types
 * so that the framework does not generate swagger model or parameter
 * information for these specific types
 *
 * @author akulkarni
 *
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @see org.springframework.boot.autoconfigure.web.ErrorProperties
 */
@RestController
public class BasicErrorController implements ErrorController {

	/** Constant for the logger for this class */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BasicErrorController.class);

	/** The error attributes. */
	@Autowired(required = false)
	private ErrorAttributes errorAttributes;

	/**
	 * Handle error.
	 *
	 * @param webRequest
	 *            the web request
	 * @param response
	 *            the response
	 * @return the response entity
	 */
	@RequestMapping(value = "/error")
	public ResponseEntity<ProviderResponse> handleError(WebRequest webRequest, HttpServletResponse response) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION.getMessage();

		if (response != null) {
			try {
				httpStatus = HttpStatus.valueOf(response.getStatus());
			} catch (IllegalArgumentException e) {
				LOGGER.warn(
						"IllegalArgumentException raised for the specified numeric value. Setting as Internal Error {}",
						e);
				// for invalid status code, set it to internal error
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		final Map<String, Object> error = getErrorAttributes(webRequest, false);
		if (error != null) {
			message = (String) error.getOrDefault("message", MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION.getMessage());
		}

		ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.ERROR, httpStatus.name(), message, httpStatus);

		return new ResponseEntity<>(providerResponse, HttpHeadersUtil.buildHttpHeadersForError(), httpStatus);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.boot.web.servlet.error.ErrorController#getErrorPath()
	 */
	@Override
	public String getErrorPath() {
		return "/error";
	}

	/**
	 * Gets the error attributes.
	 *
	 * @param webRequest
	 *            the web request
	 * @param includeStackTrace
	 *            the include stack trace
	 * @return the error attributes
	 */
	private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		if (errorAttributes != null) {
			return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
		} else {
			return null;
		}
	}
}