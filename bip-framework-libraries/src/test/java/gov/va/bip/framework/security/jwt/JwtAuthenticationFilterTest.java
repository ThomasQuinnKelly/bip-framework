package gov.va.bip.framework.security.jwt;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.security.PersonTraits;
import gov.va.bip.framework.security.config.BipSecurityTestConfig;
import gov.va.bip.framework.security.handler.JwtAuthenticationEntryPoint;
import gov.va.bip.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.bip.framework.security.util.GenerateToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BipSecurityTestConfig.class)
public class JwtAuthenticationFilterTest {

	@Autowired
	JwtAuthenticationProperties properties;

	@Autowired
	AuthenticationProvider provider;
	

	@Test
	public void testNormalOperation() throws Exception {
		/* MAKE SURE PROPERTIES ARE CORRECT */
		final String secret = "secret";
		final String issuer = "Vets.gov";
		properties.setSecret(secret);
		properties.setIssuer(issuer);
		properties.setFilterProcessUrls(new String[] { "/**" });
		properties.setExcludeUrls(new String[] { "/v2/api-docs/**", "/configuration/ui/**", "/swagger-resources/**",
				"/configuration/security/**", "/swagger-ui.html", "/webjars/**", "/**/token", "/**/swagger/error-keys.html" });
		/* done properties */

		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		request.addHeader("Authorization", "Bearer " + GenerateToken.generateJwt());
		
		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);

		final Authentication result = filter.attemptAuthentication(request, new MockHttpServletResponse());
		Assert.assertTrue(result != null);
		Assert.assertTrue(
				((PersonTraits) result.getPrincipal()).getFirstName().equalsIgnoreCase(GenerateToken.person().getFirstName()));
	}

	@Test
	public void testExceptionOperation() {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		request.addHeader("Authorization", "Bearers " + GenerateToken.generateJwt());
		
		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);
		
		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", authenticationEntryPoint);

		try {
			filter.attemptAuthentication(request, new MockHttpServletResponse());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testTamperedException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer " + GenerateToken.generateJwt() + "s");
		
		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);

		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", mock(AuthenticationEntryPoint.class));
		try {
			filter.attemptAuthentication(request, response);
		} catch (final Exception e) {
			verify(response, times(1)).getOutputStream();
			//Assert.assertTrue(e.getMessage().contains("Tampered"));
		}
	}

	@Test
	public void testMalformedException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer malformedToken");

		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);
		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);
		
		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", authenticationEntryPoint);
		
		try {
			filter.attemptAuthentication(request, response);
		} catch (final Exception e) {
			verify(response, times(1)).getOutputStream();
		}
	}
	
	@Test
	public void testInvalidTokenAndCommenceException() {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = Mockito.spy(JwtAuthenticationEntryPoint.class);
		request.addHeader("Authorization", "Bearers " + GenerateToken.generateJwt());

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, jwtAuthenticationEntryPoint);
		
		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", jwtAuthenticationEntryPoint);

		try {
			filter.attemptAuthentication(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testJwtMalformedAndCommenceException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = Mockito.spy(JwtAuthenticationEntryPoint.class);
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer malformedToken");

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, jwtAuthenticationEntryPoint);
		
		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", jwtAuthenticationEntryPoint);
		
		final Authentication result = filter.attemptAuthentication(request, response);
		Assert.assertTrue(result == null);
	}
	
	@Test
	public void testTamperedAndCommenceException() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = Mockito.spy(JwtAuthenticationEntryPoint.class);
		final String content = "{\n" + "  \"participantID\": 0,\n" + "  \"ssn\": \"string\"\n" + "}";
		request.setContent(content.getBytes());
		request.addHeader("Authorization", "Bearer " + GenerateToken.generateJwt() + "s");

		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, jwtAuthenticationEntryPoint);

		ReflectionTestUtils.setField(filter, "jwtAuthenticationEntryPoint", jwtAuthenticationEntryPoint);
		
		final Authentication result = filter.attemptAuthentication(request, response);
		Assert.assertTrue(result == null);
	}

	@Test
	public void testSuccessfulAuthentication() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		final MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain mockChain = mock(FilterChain.class);
		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);
		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);
		Authentication mockAuthentication = mock(Authentication.class);
		filter.successfulAuthentication(request, response, mockChain, mockAuthentication);
		verify(mockChain, times(1)).doFilter(request, response);
	}

	@Test
	public void testUnsuccessfulAuthentication() throws Exception {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		AuthenticationException mockException = mock(AuthenticationException.class);
		AuthenticationEntryPoint authenticationEntryPoint = mock(AuthenticationEntryPoint.class);
		final JwtAuthenticationFilter filter =
				new JwtAuthenticationFilter(properties, new JwtAuthenticationSuccessHandler(), provider, authenticationEntryPoint);
		filter.unsuccessfulAuthentication(request, response, mockException);
		verify(response, atLeast(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
		verify(response, atLeast(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, mockException.getLocalizedMessage());

	}
}