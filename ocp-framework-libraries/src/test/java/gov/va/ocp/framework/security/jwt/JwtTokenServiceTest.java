package gov.va.ocp.framework.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.ocp.framework.security.config.OcpSecurityTestConfig;
import gov.va.ocp.framework.security.jwt.JwtTokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcpSecurityTestConfig.class)
public class JwtTokenServiceTest {

	@Autowired
	JwtTokenService jwtTokenService;

	public JwtTokenServiceTest() {
	}

	/**
	 * Test of getTokenFromRequest method, of class JwtTokenService.
	 */
	@Test
	public void testGetTokenFromRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		MockitoAnnotations.initMocks(this);
		request.addHeader("Authorization", "test");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		Map<String, String> result = jwtTokenService.getTokenFromRequest();
		assertEquals(1, result.size());
		assertTrue(result.containsKey("Authorization"));
	}

	/**
	 * Test of getTokenFromRequest method, of class JwtTokenService.
	 */
	@Test
	public void testGetTokenFromRequestZeroResult() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		MockitoAnnotations.initMocks(this);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		Map<String, String> result = jwtTokenService.getTokenFromRequest();
		assertEquals(0, result.size());
	}
}
