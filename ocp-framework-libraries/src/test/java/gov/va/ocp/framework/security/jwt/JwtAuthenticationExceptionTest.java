package gov.va.ocp.framework.security.jwt;

import org.junit.Test;

import gov.va.ocp.framework.security.jwt.JwtAuthenticationException;

public class JwtAuthenticationExceptionTest {

	public JwtAuthenticationExceptionTest() {
	}

	@Test
	public void testSomeMethod() {
		new JwtAuthenticationException("testmessage");
	}

	@Test
	public void testSomeMethod1() {
		new JwtAuthenticationException("testmessage", new Throwable());
	}

}
