package gov.va.bip.framework.test.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.rest.BaseStepDef;
import gov.va.bip.framework.test.utils.wiremock.server.WireMockServerInstance;

public class BaseStepDefTest {

	BaseStepDef subject = new BaseStepDef();


	private static final String LOCALHOST_URL_PERSON = "http://localhost:9999/person";
	private static final String LOCALHOST_MULTIPART_URL_PERSON = "http://localhost:9999/multipart/person";
	private static final String SUBMIT_PAYLOAD_TXT = "submitpayload.txt";

	@BeforeClass
	public static void setup() {
		WireMockServerInstance.getWiremockserver().start();
		setupStub();
	}

	@Before
	public void init() {
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "application/json");
		subject.passHeaderInformation(tblHeader);
		try {
			subject.initREST();
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
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
		addPostBearerStub();
		addPostMultiPart();
	}

	private static void addGetPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(get(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	private static void addPostPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-person-response.json")));
	}

	private static void addDeletePersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(delete(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/delete-person-response.json")));
	}

	private static void addPutPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-person-response.json")));
	}

	private static void addPostBearerStub() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}

	private static void addPostMultiPart() {
		WireMockServerInstance.getWiremockserver().stubFor(post(urlEqualTo("/multipart/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-multipart-person-response.json")));
	}

	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.headerMap.size()));
	}

	@Test
	public void test_invokeAPIUsingDelete_Success() {
		try {
			subject.invokeAPIUsingDelete(LOCALHOST_URL_PERSON, false);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingDelete_WithBearerToken_Success() {
		try {
			subject.invokeAPIUsingDelete(LOCALHOST_URL_PERSON, true);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPut_Success() {
		try {
			subject.invokeAPIUsingPut(LOCALHOST_URL_PERSON, false);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPut_WithBearerToken_Success() {
		try {
			subject.invokeAPIUsingPut(LOCALHOST_URL_PERSON, true);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPost_Success() {
		try {
			subject.invokeAPIUsingPost(LOCALHOST_URL_PERSON, false);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingPost_WithBearerToken_Success() {
		try {
			subject.invokeAPIUsingPost(LOCALHOST_URL_PERSON, true);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
	}

	@Test
	public void test_invokeAPIUsingGet_Success() {
		try {
			subject.invokeAPIUsingGet(LOCALHOST_URL_PERSON, false);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
	}

	@Test
	public void test_invokeAPIUsingGet_WithBearerToken_Success() {
		try {
			subject.invokeAPIUsingGet(LOCALHOST_URL_PERSON, true);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
	}

	@Test
	public void test_invokeAPIUsingPostWithMultiPart_Success() {
		// Overwrite header with multi part
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "multipart/form-data");
		subject.passHeaderInformation(tblHeader);
		subject.invokeAPIUsingPostWithMultiPart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_TXT);
		assertThat(true, equalTo(!subject.strResponse.isEmpty()));
		subject.validateStatusCode(200);
	}

	@Test
	public void test_invokeAPIUsingPostWithMultiPart_InvalidFile_Failed() {
		subject.invokeAPIUsingPostWithMultiPart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
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
		subject.invokeAPIUsingPostWithMultiPart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
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

		subject.invokeAPIUsingPostWithMultiPart(LOCALHOST_MULTIPART_URL_PERSON, "invaliddocument.txt",
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
