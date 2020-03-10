package gov.va.bip.framework.test.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeferredCloseRestTemplateTest {

	private static final MediaType DEFERRED_MEDIA_TYPE = MediaType.APPLICATION_JSON;

	private DeferredCloseRestTemplate instance;

	@Mock
	private ClientHttpRequestFactory mockClientHttpRequestFactory;

	@Mock
	private ClientHttpRequest mockClientHttpRequest;

	@Mock
	private ClientHttpResponse mockClientHttpResponse;

	@Mock
	private HttpHeaders mockHttpHeaders;

	@Mock
	private ResponseErrorHandler mockResponseErrorHandler;

	private URI testUri;
	private HttpMethod testHttpMethod;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(DEFERRED_MEDIA_TYPE);

		instance = new DeferredCloseRestTemplate(mediaTypes) {
			public ClientHttpRequestFactory getRequestFactory() {
				return mockClientHttpRequestFactory;
			}
			public ResponseErrorHandler getErrorHandler() {
				return mockResponseErrorHandler;
			}
		};

		testUri = URI.create("http://localhost:1234");
		testHttpMethod = HttpMethod.GET;
	}

	@After
	public void teardown() {

	}

	@Test
	public void test_doExecute() throws IOException {
		//Setup
		when(mockClientHttpRequestFactory.createRequest(any(URI.class), any())).thenReturn(mockClientHttpRequest);
		when(mockClientHttpRequest.execute()).thenReturn(mockClientHttpResponse);
		when(mockClientHttpResponse.getHeaders()).thenReturn(mockHttpHeaders);
		when(mockHttpHeaders.getContentType()).thenReturn(DEFERRED_MEDIA_TYPE);
		when(mockResponseErrorHandler.hasError(mockClientHttpResponse)).thenReturn(false);

		//Test
		instance.doExecute(testUri, testHttpMethod, null, null);

		//Verify
		verify(mockClientHttpResponse, times(0)).close();
	}

	@Test
	public void test_doExecute_IOException() throws IOException {
		//Setup
		when(mockClientHttpRequestFactory.createRequest(any(URI.class), any())).thenReturn(mockClientHttpRequest);
		when(mockClientHttpRequest.execute()).thenReturn(mockClientHttpResponse);
		when(mockClientHttpResponse.getHeaders()).thenReturn(mockHttpHeaders);
		when(mockHttpHeaders.getContentType()).thenReturn(DEFERRED_MEDIA_TYPE);
		when(mockResponseErrorHandler.hasError(mockClientHttpResponse)).thenThrow(new IOException());

		//Test
		try {
			instance.doExecute(testUri, testHttpMethod, null, null);
			//Verify
			fail("Should have thrown an exception.");
		} catch (ResourceAccessException ex) {
			assertNotNull(ex);
		}
	}
}
