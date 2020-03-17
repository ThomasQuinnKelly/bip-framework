package gov.va.bip.framework.security.jwt;

import gov.va.bip.framework.security.config.BipSecurityTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BipSecurityTestConfig.class)
public class JwtAuthenticationPropertiesTest {

	@Autowired
	JwtAuthenticationProperties jwtAuthenticationProperties;

	public JwtAuthenticationPropertiesTest() {
	}

	/**
	 * Test of isEnabled method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testIsEnabled() {
		boolean expResult = true;
		boolean result = jwtAuthenticationProperties.isEnabled();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setEnabled method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetEnabled() {
		boolean enabled = true;
		jwtAuthenticationProperties.setEnabled(enabled);
		assertTrue(jwtAuthenticationProperties.isEnabled());
	}

	/**
	 * Test of getHeader method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetHeader() {
		String expResult = "Authorization";
		String result = jwtAuthenticationProperties.getHeader();
		assertTrue(expResult.equals(result));
	}

	/**
	 * Test of getSecret method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetSecret() {
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		String expResult = "secret";
		String result = jwtAuthenticationProperties.getSecret();
		assertTrue(expResult.equals(result));
	}

	/**
	 * Test of setSecret method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetSecret() {
		String secret = "secret_key_consumer";
		jwtAuthenticationProperties.setSecret(secret);
		assertTrue(secret.equals(jwtAuthenticationProperties.getSecret()));
	}

	/**
	 * Test of getIssuer method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetIssuer() {
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		String expResult = "Vets.gov";
		String result = jwtAuthenticationProperties.getIssuer();
		assertTrue(expResult.equals(result));
	}

	/**
	 * Test of setSecret method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetExpirationInSeconds() {
		int expireInSeconds = 900;
		jwtAuthenticationProperties.setExpireInSeconds(expireInSeconds);
		assertTrue(expireInSeconds == jwtAuthenticationProperties.getExpireInSeconds());
	}

	/**
	 * Test of getIssuer method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetExpirationInSeconds() {
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		int expResult = 900;
		int result = jwtAuthenticationProperties.getExpireInSeconds();
		assertTrue(expResult == result);
	}

	/**
	 * Test of setSecret method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetIssuer() {
		String issuer = "Issuer2.gov";
		jwtAuthenticationProperties.setIssuer(issuer);
		assertTrue(issuer.equals(jwtAuthenticationProperties.getIssuer()));
	}

	/**
	 * Test of getFilterProcessUrl method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetFilterProcessUrl() {
		String expResult = "url";
		String[] results = jwtAuthenticationProperties.getFilterProcessUrls();
		for (String result : results) {
			assertTrue(expResult.equals(result));
		}
	}

	/**
	 * Test of setFilterProcessUrl method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetFilterProcessUrl() {
		String filterProcessUrl = "url";
		jwtAuthenticationProperties.setFilterProcessUrls(new String[] { filterProcessUrl });
		assertTrue(filterProcessUrl.equals(jwtAuthenticationProperties.getFilterProcessUrls()[0]));
	}

	/**
	 * Test of getExcludeUrls method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testGetExcludeUrls() {
		String[] result = jwtAuthenticationProperties.getExcludeUrls();
		assertTrue(result.length > 0);
	}

	/**
	 * Test of setExcludeUrls method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetExcludeUrls() {
		String[] excludeUrls = { "http://localhost:8762/api/bip-reference-person/swagger-ui.html" };
		jwtAuthenticationProperties.setExcludeUrls(excludeUrls);
		assertTrue(jwtAuthenticationProperties.getExcludeUrls().length > 0);
	}
	
	/**
	 * Test of setSecret method, of class JwtAuthenticationProperties.
	 */
	@Test
	public void testSetJwtKeyPairs() {
		List<JwtAuthenticationProperties.JwtKeyPairs> listKeyPairs = new ArrayList<JwtAuthenticationProperties.JwtKeyPairs>();
		listKeyPairs.add(new JwtAuthenticationProperties.JwtKeyPairs("secret1","issuer1"));
		jwtAuthenticationProperties.setKeyPairs(listKeyPairs);
		assertTrue("secret1".equals(jwtAuthenticationProperties.getKeyPairs().get(0).getSecret()));
		assertTrue("issuer1".equals(jwtAuthenticationProperties.getKeyPairs().get(0).getIssuer()));
		jwtAuthenticationProperties.setKeyPairs(Collections.emptyList());
	}

}
