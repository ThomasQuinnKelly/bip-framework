package gov.va.bip.framework.test.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.rest.BaseStepDefHandler;
import gov.va.bip.framework.test.utils.wiremock.server.WireMockServerInstance;

/**
 * The Class BaseStepDefHandlerTest.
 */
public class BaseStepDefHandlerTest {

	BaseStepDefHandler subject = new BaseStepDefHandler();

	private static final String LOCALHOST_URL_PERSON = "http://localhost:9999/person";

	@BeforeClass
	public static void setup() {
		WireMockServerInstance.getWiremockserver().start();
		addGetPersonStub();
	}

	@AfterClass
	public static void teardown() {
		WireMockServerInstance.getWiremockserver().stop();
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
		assertThat(true, equalTo(subject.getRestUtil() != null));
		assertThat(true, equalTo(subject.getRestConfig() != null));
	}

	private static void addGetPersonStub() {
		WireMockServerInstance.getWiremockserver().stubFor(
				get(urlEqualTo("/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.getHeaderMap().size()));
	}

	@Test
	public void test_getResponse_Success() {
		subject.invokeAPIUsingGet(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!subject.getStrResponse().isEmpty()));
	}

}
