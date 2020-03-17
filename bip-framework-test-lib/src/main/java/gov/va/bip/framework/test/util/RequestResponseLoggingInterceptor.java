package gov.va.bip.framework.test.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Intercepts API calls to log and troubleshoot header, request and response.
 * 
 * @author sravi
 *
 */
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

	/** How many bytes for inclusion in the log audit */
	public static final int NO_OF_BYTES_TO_LIMIT_LOG_OBJECT = 1024;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.http.client.ClientHttpRequestInterceptor#intercept(
	 * org.springframework.http.HttpRequest, byte[],
	 * org.springframework.http.client.ClientHttpRequestExecution)
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
	 * Function that logs header, body and other parameters before API call. It
	 * logs all info in debug.
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
		if (body != null && LOGGER.isDebugEnabled()) {
			if (request.getHeaders().getAccept()
					.contains(MediaType.APPLICATION_OCTET_STREAM)
					|| (request.getHeaders().getContentType() != null && (request.getHeaders().getContentType()
							.includes(MediaType.APPLICATION_OCTET_STREAM)
							|| request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)))) {
				String strByteStream = copyLimitBodyBytes(new ByteArrayInputStream(body));
				LOGGER.debug("Request of type binary. Limit bytes to: {} Byte String: {}",
						NO_OF_BYTES_TO_LIMIT_LOG_OBJECT, strByteStream);
			} else {
				LOGGER.debug("Request body: {}", new String(body, Charset.defaultCharset()));
			}
		}
		LOGGER.debug("==========================request end================================================");
	}

	/**
	 * Function that logs response, status code parameters after API call. It
	 * logs all info in debug.
	 * 
	 * @param response
	 * @throws IOException
	 */
	private void logResponse(ClientHttpResponse response) throws IOException {
		LOGGER.debug("============================response begin==========================================");
		if (response == null) {
			LOGGER.debug("No client-side HTTP response (Returned Null)");
		} else {
			LOGGER.debug("Status code  : {}", response.getStatusCode());
			LOGGER.debug("Status text  : {}", response.getStatusText());
			LOGGER.debug("Headers      : {}", response.getHeaders());
			if (LOGGER.isDebugEnabled()) {
				if (response.getHeaders().getAccept()
						.contains(MediaType.APPLICATION_OCTET_STREAM)
						|| (response.getHeaders().getContentType() != null && (response.getHeaders().getContentType()
								.includes(MediaType.APPLICATION_OCTET_STREAM)
								|| response.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)))) {
					String strByteStream = copyLimitBodyBytes(response.getBody());
					LOGGER.debug("Response of type binary. Limit bytes to: {} Byte String: {}",
							NO_OF_BYTES_TO_LIMIT_LOG_OBJECT, strByteStream);
				} else {
					LOGGER.debug("Response body: {}",
							StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
				}
			}
		}
		LOGGER.debug("=======================response end=================================================");
	}

	/**
	 * Copy and limit body bytes.
	 *
	 * @param body
	 *            the body
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String copyLimitBodyBytes(InputStream body) throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		IOUtils.copyLarge(body, byteOutput, 0, NO_OF_BYTES_TO_LIMIT_LOG_OBJECT);
		return new String(byteOutput.toByteArray());
	}

}