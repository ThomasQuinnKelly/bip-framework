package gov.va.ocp.framework.security.handler;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import gov.va.ocp.framework.security.handler.JwtAuthenticationEntryPoint;

public class JwtAuthenticationEntryPointTest {

	public JwtAuthenticationEntryPointTest() {
	}

	/**
	 * Test of commence method, of class JwtAuthenticationEntryPoint.
	 */
	@Test
	public void testCommence() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user");
		MockHttpServletResponse response = new MockHttpServletResponse();
		JwtAuthenticationEntryPoint instance = new JwtAuthenticationEntryPoint();
		instance.commence(request, response, mock(AuthenticationException.class));
	}

}
