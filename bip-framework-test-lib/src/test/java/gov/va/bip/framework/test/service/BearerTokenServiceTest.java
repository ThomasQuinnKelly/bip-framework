package gov.va.bip.framework.test.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import gov.va.bip.framework.test.service.BearerTokenService;

public class BearerTokenServiceTest {

	private static WireMockServer wireMockServer;

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
		addPostBearerStub();
	}

	private static void addPostBearerStub() {
		wireMockServer.stubFor(post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}

	@Test
	public void test_getToken_Success() {
		String token = BearerTokenService.getInstance().getBearerToken();
		assertThat(true, equalTo(!token.isEmpty()));
	}

	@Test
	public void test_getTokenByHeaderFile_Success() {
		String token = BearerTokenService.getTokenByHeaderFile("token.Request");
		assertThat(true, equalTo(!token.isEmpty()));
	}

}
