package gov.va.bip.framework.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class RequestResponseLoggingInterceptorTest {
	
	/** The response mock. */
	private ResponseMock responseMock = new ResponseMock();
	
	@Before
	public void setUp() throws Exception {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);
		rootLogger.setLevel(Level.DEBUG);
	}

	@Test
	public void interceptWithNullResponse() throws Exception {
		Request request = new Request();
		new RequestResponseLoggingInterceptor()
                        .intercept(request, null, new RequestExecutionNullResponse());
	}
	
	@Test
	public void interceptWithMock() throws Exception {
		Request request = new Request();
		new RequestResponseLoggingInterceptor()
                        .intercept(request, null, new RequestExecutionMockResponse());
	}
	
	@Test
	public void interceptWithMockAndBody() throws Exception {
		final byte[] byteBody = "Foo".getBytes();
		Request request = new Request();
		new RequestResponseLoggingInterceptor()
                        .intercept(request, byteBody, new RequestExecutionMockResponse());
	}

	/**
	 * The Class Request.
	 */
	private class Request implements HttpRequest {

		HttpHeaders headers = new HttpHeaders();

		@Override
		public HttpMethod getMethod() {
			return null;
		}

		@Override
		public URI getURI() {
			return null;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}

		@Override
		public String getMethodValue() {
			return null;
		}
	}

	/**
	 * The Class RequestExecutionNullResponse.
	 */
	private class RequestExecutionNullResponse implements ClientHttpRequestExecution {

		@Override
		public ClientHttpResponse execute(HttpRequest request, byte[] body) throws 
                        IOException {
			return null;
		}

	}
	
	/**
	 * The Class RequestExecutionMockResponse.
	 */
	private class RequestExecutionMockResponse implements ClientHttpRequestExecution {

		@Override
		public ClientHttpResponse execute(HttpRequest request, byte[] body) throws 
                        IOException {
			return responseMock;
		}

	}
	
	/**
	 * The Class ResponseMock.
	 */
	private static class ResponseMock implements ClientHttpResponse {

		private HttpStatus statusCode = HttpStatus.OK;

		private String statusText = "";

		private HttpHeaders headers = new HttpHeaders();

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return statusCode;
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return statusCode.value();
		}

		@Override
		public String getStatusText() throws IOException {
			return statusText;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}

		@Override
		public InputStream getBody() throws IOException {
			return null;
		}

		@Override
		public void close() {
		}
	}
}