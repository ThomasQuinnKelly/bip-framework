package gov.va.bip.framework.test.rest;

import gov.va.bip.framework.test.utils.wiremock.server.WireMockServerInstance;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * The Class BaseStepDefHandlerTest.
 */
public class BaseStepDefHandlerTest {

	BaseStepDefHandler subject = new BaseStepDefHandler();

	BaseStepDefHandler subjectWithCustomMsgConverter = null;

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
		assertThat(true, equalTo(subject.getRestUtil() != null));
		assertThat(true, equalTo(subject.getRestConfig() != null));

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ResourceRegionHttpMessageConverter());
		subjectWithCustomMsgConverter = new BaseStepDefHandler(messageConverters);
		assertThat(true, equalTo(subjectWithCustomMsgConverter.getRestUtil() != null));
		assertThat(true, equalTo(subjectWithCustomMsgConverter.getRestConfig() != null));
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
		assertThat(true, equalTo(
				!subject.getStrResponse().isEmpty() ||
				subject.getObjResponse() != null
				));
	}

	@Test
	public void test_getObjResponse_Success() {
		subject.invokeAPIUsingGet(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(
				subject.getObjResponse() == null
				));
	}

}
