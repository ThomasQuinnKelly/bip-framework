package gov.va.bip.framework.test.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.service.BearerTokenService;
import gov.va.bip.framework.test.utils.wiremock.server.WireMockServerInstance;

public class BearerTokenServiceTest {

	private static final String TOKEN_URL_PROPERTY_KEY = "tokenUrl";
	private static final String COULD_NOT_FIND_PROPERTY_STRING = "Could not find property : ";
	private static final String BASE_URL_PROPERTY_KEY = "baseURL";

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
		addPostBearerStub();
	}

	private static void addPostBearerStub() {
		WireMockServerInstance.getWiremockserver().stubFor(
				post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}

	@Test
	public void test_getToken_Success() {
		String token = null;
		try {
			token = BearerTokenService.getInstance().getBearerToken();
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(!token.isEmpty()));
	}

	@Test
	public void test_getTokenByHeaderFile_Success() {
		String token = null;
		token = BearerTokenService.getTokenByHeaderFile("token.request");
		assertThat(true, equalTo(!token.isEmpty()));
	}

	@Test
	public void test_handleNullUrls_bothNull() throws Throwable {
		Method m;
		try {
			m = BearerTokenService.class.getDeclaredMethod("handleNullUrls", String.class, String.class);
			m.setAccessible(true);
			m.invoke(null, "", "");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage().contains(COULD_NOT_FIND_PROPERTY_STRING));
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage()
					.contains(BASE_URL_PROPERTY_KEY + ", " + TOKEN_URL_PROPERTY_KEY));
		}
	}

	@Test
	public void test_handleNullUrls_baseUrlNull() throws Throwable {
		Method m;
		try {
			m = BearerTokenService.class.getDeclaredMethod("handleNullUrls", String.class, String.class);
			m.setAccessible(true);
			m.invoke(null, "", "");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage().contains(COULD_NOT_FIND_PROPERTY_STRING));
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage().contains(BASE_URL_PROPERTY_KEY));
		}
	}

	@Test
	public void test_handleNullUrls_tokenUrlNull() throws Throwable {
		Method m;
		try {
			m = BearerTokenService.class.getDeclaredMethod("handleNullUrls", String.class, String.class);
			m.setAccessible(true);
			m.invoke(null, "", "");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage().contains(COULD_NOT_FIND_PROPERTY_STRING));
			assertTrue(((InvocationTargetException) e).getTargetException().getMessage().contains(TOKEN_URL_PROPERTY_KEY));
		}
	}

}
