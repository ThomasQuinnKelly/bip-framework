package gov.va.bip.framework.test.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.net.HttpHeaders;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.service.RESTConfigService;
import gov.va.bip.framework.test.utils.wiremock.server.WireMockServerInstance;

public class RESTUtilTest {

	RESTUtil restUtil = new RESTUtil();
	private static final String URL_PERSON = "/person";
	private static final String URL_PERSON_DOCUMENT = "/person/pid/document";
	private static final String LOCALHOST_URL_PERSON = "http://localhost:9999/person";
	private static final String LOCALHOST_URL_PERSON_DOCUMENT = "http://localhost:9999/person/pid/document";
	private static final String LOCALHOST_MULTIPART_URL_PERSON = "http://localhost:9999/multipart/person";
	private static final String SUBMIT_PAYLOAD_TXT = "submitpayload.txt";
	private static final String SUBMIT_PAYLOAD_JSON = "submitpayload.json";
	private static final String SUBMIT_PAYLOAD_NO_EXT = "submitpayload";

	@BeforeClass
	public static void setup() {
		WireMockServerInstance.getWiremockserver().start();
		setupStub();
	}

	@AfterClass
	public static void teardown() {
		WireMockServerInstance.getWiremockserver().stop();
	}

	public static void setupStub() {
		addGetPersonStub();
		addPostPersonStub();
		addDeletePersonStub();
		addPutPersonStub();
		addPostMultiPart();
		addGetPerson500Stub();
	}

	private static void addGetPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(get(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(HttpStatus.OK_200).withBodyFile("json/get-person-response.json")));
	}

	private static void addPostPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(HttpStatus.OK_200).withBodyFile("json/post-person-response.json")));
	}

	private static void addPutPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(put(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(HttpStatus.OK_200).withBodyFile("json/put-person-response.json")));
	}

	private static void addDeletePersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(delete(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(HttpStatus.OK_200).withBodyFile("json/delete-person-response.json")));
	}

	private static void addPostMultiPart() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo("/multipart/person"))
				.willReturn(aResponse().withStatus(HttpStatus.OK_200).withBodyFile("json/post-multipart-person-response.json")));
	}
	
	private static void addGetPerson500Stub() {
		WireMockServerInstance.getWiremockserver().stubFor(get(urlEqualTo(URL_PERSON_DOCUMENT))
				.willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR_500).withBodyFile("json/get-person-doc-error-response.json")));
	}

	@Test
	public void test_setUpRequest_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, "application/json");
		restUtil.setUpRequest(mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get(HttpHeaders.AUTHORIZATION).get(0)));
	}

	@Test
	public void test_setUpRequest_WithBody_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		restUtil.setUpRequest("janedoe.request", mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get(HttpHeaders.AUTHORIZATION).get(0)));
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_setUpRequest_BadFile_Failed() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		RESTUtil restUtilSpied = org.mockito.Mockito.spy(restUtil);
		try {
			doThrow(new IOException()).when(restUtilSpied).readFile(org.mockito.ArgumentMatchers.any(File.class));
		} catch (IOException e) {
			e.printStackTrace();
			fail("exception not expected");
		}
		restUtilSpied.setUpRequest("janedoebad^^@.%%.request", mapHeader);
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_validKeyStore() {
		String response = restUtil.getResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void testGetRestTemplate() {
		Constructor<RESTConfigService> constructor;
		try {
			constructor = RESTConfigService.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			RESTConfigService config = constructor.newInstance();
			Properties prop = new Properties();
			prop.setProperty("javax.net.ssl.keyStore", "");
			ReflectionTestUtils.setField(config, "prop", prop);
			Field instanceOfRESTConfigService = RESTConfigService.class.getDeclaredField("instance");
			instanceOfRESTConfigService.setAccessible(true);
			instanceOfRESTConfigService.set(null, config);
			ReflectionTestUtils.invokeMethod(new RESTUtil(), "getRestTemplate");
			// reset the field instance and prop fields
			instanceOfRESTConfigService.set(null,null);
			ReflectionTestUtils.setField(config, "prop", null);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
	}

	@Test
	public void test_getResponse_WithRetry() {
		String response = restUtil.getResponse("http://localhost:9999/urldoesnotexits");
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_setUpRequest_WithBody_Failed() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		try {
			restUtil.setUpRequest("nonexistsfile.request", mapHeader);
		} catch (BipTestLibRuntimeException e) {
			assertTrue(e.getMessage().contains("Requested File Doesn't Exist: request/"));
		}
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_Success() {
		String response = restUtil.getResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
		assertThat(true, equalTo(restUtil.getResponseHttpHeaders() != null));
	}

	@Test
	public void test_getResponse_Failed() {
		restUtil.getResponse("http://localhost:9999/urldoesnotexits");
	}

	@Test
	public void test_postResponse_Success() {
		String response = restUtil.postResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_putResponse_Success() {
		String response = restUtil.putResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_deleteResponse_Success() {
		String response = restUtil.deleteResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_TXT, false, "");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}
	
	@Test
	public void test_postResponseWithMultipartPayloadRequestPart_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_JSON, true, "");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}
	
	@Test
	public void test_postResponseWithMultipartPayloadRequestParam_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_JSON, false, "");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}
	
	@Test
	public void test_postResponseWithMultipartPayloadRequestParamNoExtension_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_NO_EXT, false, "");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}
	
	@Test
	public void test_postResponseWithMultipartMultiValueMap_Success() {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, body, null);
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}
	
	@Test
	public void test_postResponseWithMultipartMultiValueMapMediaType_Success() {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, body, MediaType.MULTIPART_FORM_DATA);
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}

	@Test
	public void test_postResponseWithMultipart__mbfile_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "IS_25mb.txt",
				SUBMIT_PAYLOAD_TXT, false, "");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}

	@Test
	public void test_postResponseWithMultipart_InvalidPayload() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				"invalidpayload.txt", false, "");
		assertNotNull(response);
	}

	@Test
	public void test_readExpectedResponse_Success() {
		String response = restUtil.readExpectedResponse("test.response");
		boolean isBodyEmpty = response.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_readExpectedResponse_FileNotExist_Success() {
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("response/" + "badfile.response");
		File strFilePath;
		try {
			strFilePath = new File(urlFilePath.toURI());
			strFilePath.setReadable(false);
			String response = restUtil.readExpectedResponse("badfile1.response");
			strFilePath.setReadable(true);
			assertThat(null, equalTo(response));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test_readExpectedResponse_FileNotExists_Success() {
		String response = restUtil.readExpectedResponse("nonexistsfile.response");
		assertThat(null, equalTo(response));
	}
	
	@Test
	public void test_getResponse_PersonDocument() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		restUtil.setUpRequest("janedoe.request", mapHeader);
		String response = restUtil.getResponse(LOCALHOST_URL_PERSON_DOCUMENT);
		assertThat(true, equalTo(!response.isEmpty()));
		assertThat(true, equalTo(restUtil.getResponseHttpHeaders() != null));
	}

}
