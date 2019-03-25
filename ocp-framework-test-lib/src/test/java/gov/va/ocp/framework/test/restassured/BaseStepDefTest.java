package gov.va.ocp.framework.test.restassured;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

public class BaseStepDefTest {

	BaseStepDef subject = new BaseStepDef();

	private static WireMockServer wireMockServer;

	@BeforeClass
	public static void setup() {
		wireMockServer = new WireMockServer(9999);
		wireMockServer.start();
		setupStub();
	}

	@Before
	public void init() {
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "application/json");
		subject.passHeaderInformation(tblHeader);
		subject.initREST();
	}

	@AfterClass
	public static void teardown() {
		wireMockServer.stop();
	}

	public static void setupStub() {
		addGetPersonStub();
		addPostPersonStub();
		addDeletePersonStub();
		addPostBearerStub();
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

	private static void addDeletePersonStub() {
		wireMockServer.stubFor(delete(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/delete-person-response.json")));
	}

	private static void addPostBearerStub() {
		wireMockServer.stubFor(post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}

	private static void addPostMultiPart() {
		wireMockServer.stubFor(post(urlEqualTo("/multipart/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-multipart-person-response.json")));
	}

	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.headerMap.size()));
	}

	@Test
	public void test_invokeAPIUsingDelete_Success() {
		subject.invokeAPIUsingDelete("http://localhost:9999/person", false);
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingDelete_WithBearerToken_Success() {
		subject.invokeAPIUsingDelete("http://localhost:9999/person", true);
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPost_Success() {
		subject.invokeAPIUsingPost("http://localhost:9999/person", false);
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPost_WithBearerToken_Success() {
		subject.invokeAPIUsingPost("http://localhost:9999/person", true);
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingGet_Success() {
		subject.invokeAPIUsingGet("http://localhost:9999/person", false);
	}

	@Test
	public void test_invokeAPIUsingGet_WithBearerToken_Success() {
		subject.invokeAPIUsingGet("http://localhost:9999/person", true);
	}

	@Test
	public void test_invokeAPIUsingPostWithMultiPart_Success() {
		// Overwrite header with multi part
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "multipart/form-data");
		subject.passHeaderInformation(tblHeader);
		subject.invokeAPIUsingPostWithMultiPart("http://localhost:9999/multipart/person", "document.txt",
				"submitpayload.txt");
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
		subject.validateStatusCode(200);
	}

	@Test
	public void test_invokeAPIUsingPostWithMultiPart_InvalidFile_Failed() {
		subject.invokeAPIUsingPostWithMultiPart("http://localhost:9999/multipart/person", "document.txt",
				"invalidpayload.txt");
		assertNull(subject.strResponse);
	}

	@Test
	public void test_invokeAPIUsingPostWithMultiPart_ByteArray_Success() {
		// Overwrite header with multi part
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "multipart/form-data");
		subject.passHeaderInformation(tblHeader);
		subject.invokeAPIUsingPostWithMultiPart("http://localhost:9999/multipart/person", "document.txt",
				"HelloWorld".getBytes());
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray_InvalidPart_Failed() {
		// Overwrite header with multi part
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "multipart/form-data");
		subject.passHeaderInformation(tblHeader);

		subject.invokeAPIUsingPostWithMultiPart("http://localhost:9999/multipart/person", "invaliddocument.txt",
				"HelloWorld".getBytes());
		assertNull(subject.strResponse);
	}

	@Test
	public void test_setHeader_Success() throws Exception {
		subject.setHeader("dev-janedoe");
		assertThat(2, equalTo(subject.headerMap.size()));
	}

	@Test
	public void test_compareExpectedResponseWithActual_Success() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActual("test.response")));
	}

	@Test
	public void test_compareExpectedResponseWithActual_Failed() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(false, equalTo(subject.compareExpectedResponseWithActual("badfile.response")));
	}

	@Test
	public void test_compareExpectedResponseWithActualByRow_Success() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActualByRow("test.response")));
	}

	@Test
	public void test_compareExpectedResponseWithActualByRow_Failed() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(false, equalTo(subject.compareExpectedResponseWithActualByRow("person.response")));
	}

	@Test
	public void test_SetEnvQA_readProperty_Success() {
		System.setProperty("test.env", "qa");
	}

}
