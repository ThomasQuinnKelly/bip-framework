package gov.va.ocp.framework.test.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

public class RESTUtilTest {

	private static WireMockServer wireMockServer;
	RESTUtil restUtil = new RESTUtil();

	@BeforeClass
	public static void setup() {
		wireMockServer = new WireMockServer(9999);
		wireMockServer.start();
		setupStub();
	}

	@AfterClass
	public static void teardown() {
		wireMockServer.stop();
	}

	public static void setupStub() {
		addGetPersonStub();
		addPostPersonStub();
		addDeletePersonStub();
		addPutPersonStub();
		addPostMultiPart();
	}

	private static void addGetPersonStub() {
		wireMockServer.stubFor(get(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	private static void addPostPersonStub() {
		wireMockServer.stubFor(post(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-person-response.json")));
	}

	private static void addPutPersonStub() {
		wireMockServer.stubFor(put(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/put-person-response.json")));
	}

	private static void addDeletePersonStub() {
		wireMockServer.stubFor(delete(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/delete-person-response.json")));
	}
	private static void addPostMultiPart() {
		wireMockServer.stubFor(post(urlEqualTo("/multipart/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-multipart-person-response.json")));
	}


	@Test
	public void test_setUpRequest_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest(mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
	}

	@Test
	public void test_setUpRequest_WithBody_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest("janedoe.request", mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_setUpRequest_WithBody_Failed() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		restUtil.setUpRequest("nonexistsfile.request", mapHeader);
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_Success() {
		String response = restUtil.getResponse("http://localhost:9999/person");
		assertThat(true, equalTo(!response.isEmpty()));
	}
	@Test
	public void test_getResponse_Failed() {
		restUtil.getResponse("http://localhost:9999/invalidurl");
	}

	@Test
	public void test_postResponse_Success() {
		String response = restUtil.postResponse("http://localhost:9999/person");
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_putResponse_Success() {
		String response = restUtil.putResponse("http://localhost:9999/person");
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_deleteResponse_Success() {
		String response = restUtil.deleteResponse("http://localhost:9999/person");
		assertThat(true, equalTo(!response.isEmpty()));
	}
	
	@Test
	public void test_postResponseWithMultipart_Success() {
		String response = restUtil.postResponseWithMultipart("http://localhost:9999/multipart/person", "document.txt", "submitpayload.txt");
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}

	@Test
	public void test_postResponseWithMultipart_InvalidFile_Failed() {
		String response = restUtil.postResponseWithMultipart("http://localhost:9999/multipart/person", "document.txt", "invalidpayload.txt");
		assertNull(response);
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray_Success() {
		String response = restUtil.postResponseWithMultipart("http://localhost:9999/multipart/person", "document.txt", "HelloWorld".getBytes());
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray_InvalidPart_Failed() {
		String response = restUtil.postResponseWithMultipart("http://localhost:9999/multipart/person", "invaliddocument.txt", "HelloWorld".getBytes());
		assertNull(response);
	}

	
	@Test
	public void test_readExpectedResponse_Success() {
		String response = restUtil.readExpectedResponse("test.response");
		boolean isBodyEmpty = response.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_readExpectedResponse_FileNotExists_Success() {
		String response = restUtil.readExpectedResponse("nonexistsfile.response");
		assertThat(null, equalTo(response));
	}

}
