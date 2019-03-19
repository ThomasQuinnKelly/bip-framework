package gov.va.ocp.framework.security.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.security.core.Authentication;

import gov.va.ocp.framework.security.handler.JwtAuthenticationSuccessHandler;

public class JwtAuthenticationSuccessHandlerTest {

	public JwtAuthenticationSuccessHandlerTest() {
	}

	/**
	 * Test of onAuthenticationSuccess method, of class JwtAuthenticationSuccessHandler.
	 */
	@Test
	public void testOnAuthenticationSuccess() throws Exception {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		Authentication authentication = null;
		JwtAuthenticationSuccessHandler instance = new JwtAuthenticationSuccessHandler();
		instance.onAuthenticationSuccess(request, response, authentication);
	}

}
