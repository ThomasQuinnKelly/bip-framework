package gov.va.bip.framework.test.util;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * Intercepts API calls to log and troubleshoot header, request and response.
 * 
 * @author sravi
 *
 */
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

	/* (non-Javadoc)
	 * @see org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.http.HttpRequest, byte[], org.springframework.http.client.ClientHttpRequestExecution)
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		return response;
	}

	/**
	 * Function that logs header, body and other parameters before API call. It logs
	 * all info in debug.
	 * 
	 * @param request
	 * @param body
	 * @throws IOException
	 */
	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		LOGGER.debug("===========================request begin================================================");
		LOGGER.debug("URI         : {}", request.getURI());
		LOGGER.debug("Method      : {}", request.getMethod());
		LOGGER.debug("Headers     : {}", request.getHeaders());
		LOGGER.debug("Request body: {}", new String(body, "UTF-8"));
		LOGGER.debug("==========================request end================================================");
	}

	/**
	 * Function that logs response, status code parameters after API call. It logs
	 * all info in debug.
	 * 
	 * @param response
	 * @throws IOException
	 */
	private void logResponse(ClientHttpResponse response) throws IOException {
		LOGGER.debug("============================response begin==========================================");
		LOGGER.debug("Status code  : {}", response.getStatusCode());
		LOGGER.debug("Status text  : {}", response.getStatusText());
		LOGGER.debug("Headers      : {}", response.getHeaders());
		LOGGER.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		LOGGER.debug("=======================response end=================================================");
	}
}