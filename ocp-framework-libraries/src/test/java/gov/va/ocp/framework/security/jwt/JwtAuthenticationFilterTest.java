package gov.va.ocp.framework.security.jwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ocp.framework.security.PersonTraits;
import gov.va.ocp.framework.security.config.OcpSecurityTestConfig;
import gov.va.ocp.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationException;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationFilter;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.ocp.framework.security.util.GenerateToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcpSecurityTestConfig.class)
public class JwtAuthenticationFilterTest {

	@Autowired
	JwtAuthenticationProperties properties;

	@Autowired
	AuthenticationProvider provider;

	@Test
	public void testNormalOperation() throws Exception {
		/* MAKE SURE PROPERTIES ARE CORRECT */
		final String secret = "secret";
		properties.setSecret(secret);
		properties.setFilterProcessUrls(new String[] { "/**" });
		properties.setExcludeUrls(new String[] { "/v2/api-docs/**", "/configuration/ui/**", "/swagger-resources/**",
				"/configuration/security/**", "/swagger-ui.html", "/webjars/**", "/**/token", "/**/swagger/error-keys.html" });
		/* done properties */

		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		request.addHeader("Authorization", "Bearer " + GenerateToken.generateJwt());

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);

		final Authentication result = filter.attemptAuthentication(request, new MockHttpServletResponse());
		Assert.assertTrue(result != null);
		Assert.assertTrue(
				((PersonTraits) result.getPrincipal()).getFirstName().equalsIgnoreCase(GenerateToken.person().getFirstName()));
	}

	@Test(expected = JwtAuthenticationException.class)
	public void testExceptionOperation() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		request.addHeader("Authorization", "Bearers " + GenerateToken.generateJwt());

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);

		filter.attemptAuthentication(request, new MockHttpServletResponse());
	}

	@Test
	public void testTamperedException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer " + GenerateToken.generateJwt() + "s");

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);

		try {
			filter.attemptAuthentication(request, new MockHttpServletResponse());
		} catch (final Exception e) {
			Assert.assertTrue(e.getMessage().contains("Tampered"));
		}
	}

	@Test
	public void testMalformedException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer malformedToken");

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);
		try {
			filter.attemptAuthentication(request, new MockHttpServletResponse());
		} catch (final Exception e) {
			Assert.assertTrue(e.getMessage().contains("Malformed"));
		}
	}

	@Test
	public void testSuccessfulAuthentication() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain mockChain = mock(FilterChain.class);
		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);
		Authentication mockAuthentication = mock(Authentication.class);
		filter.successfulAuthentication(request, response, mockChain, mockAuthentication);
		verify(mockChain, times(1)).doFilter(request, response);
	}

	@Test
	public void testUnsuccessfulAuthentication() throws Exception {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		AuthenticationException mockException = mock(AuthenticationException.class);
		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider);
		filter.unsuccessfulAuthentication(request, response, mockException);
		verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
		verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, mockException.getLocalizedMessage());

	}
}
