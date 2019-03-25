package gov.va.ocp.framework.test.restassured;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

/**
 * The Class BaseStepDefHandlerTest.
 */
public class BaseStepDefHandlerTest {

	BaseStepDefHandler subject = new BaseStepDefHandler();

	private static WireMockServer wireMockServer;

	@BeforeClass
	public static void setup() {
		wireMockServer = new WireMockServer(9999);
		wireMockServer.start();
		addGetPersonStub();
	}

	@AfterClass
	public static void teardown() {
		wireMockServer.stop();
	}

	@Before
	public void init() {
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Accept", "application/json");
		tblHeader.put("Content-Type", "application/json");
		subject.passHeaderInformation(tblHeader);
		subject.initREST();
		assertThat(true, equalTo(subject.getRestUtil() != null));
		assertThat(true, equalTo(subject.getRestConfig() != null));
	}

	private static void addGetPersonStub() {
		wireMockServer.stubFor(get(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.getHeaderMap().size()));
	}

	@Test
	public void test_getResponse_Success() {
		subject.invokeAPIUsingGet("http://localhost:9999/person");
		assertThat(true, equalTo(!subject.getStrResponse().isEmpty()));
	}

}
