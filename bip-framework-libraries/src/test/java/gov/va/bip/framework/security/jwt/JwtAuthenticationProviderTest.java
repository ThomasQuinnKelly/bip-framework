package gov.va.bip.framework.security.jwt;

import gov.va.bip.framework.security.PersonTraits;
import gov.va.bip.framework.security.config.BipSecurityTestConfig;
import gov.va.bip.framework.security.model.Person;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BipSecurityTestConfig.class)
public class JwtAuthenticationProviderTest {
	@Autowired
	JwtAuthenticationProperties jwtAuthenticationProperties;

	@Autowired
	TokenResource tokenResource;

	public JwtAuthenticationProviderTest() {
	}

	/**
	 * Test of retrieveUser method, of class JwtAuthenticationProvider.
	 */
	@Test
	public void testRetrieveUser() {
		final String username = "jdoe";
		final Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		person.setCorrelationIds(
				Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
						"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		final String token = tokenResource.getToken(person);

		final JwtAuthenticationToken authentication = new JwtAuthenticationToken(token);
		final JwtParser parser = new JwtParser(jwtAuthenticationProperties);
		final JwtAuthenticationProvider instance = new JwtAuthenticationProvider(parser);
		final UserDetails result = instance.retrieveUser(username, authentication);
		assertNotNull(result);
	}

	@Test
	public void testRetrieveUser_NoPerson() {
		final String username = "jdoe";
		final Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		person.setCorrelationIds(
				Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
						"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		final String token = tokenResource.getToken(person);

		final JwtAuthenticationToken authentication = new JwtAuthenticationToken(token);
		final JwtParser parser = spy(new JwtParser(jwtAuthenticationProperties));
		final JwtAuthenticationProvider instance = new JwtAuthenticationProvider(parser);
		doReturn(null).when(parser).parseJwt(any());

		UserDetails result = null;
		try {
			result = instance.retrieveUser(username, authentication);
			fail("Should have thrown JwtAuthenticationException.");
		} catch (final Exception e) {
			assertTrue(JwtAuthenticationException.class.isAssignableFrom(e.getClass()));
			assertTrue("Invalid Token.".equals(e.getMessage()));
		}
		assertNull(result);
	}

	/**
	 * Test of retrieveUser method, of class JwtAuthenticationProvider.
	 */
	@Test(expected = MalformedJwtException.class)
	public void testRetrieveUserWithException() {

		final JwtAuthenticationToken authentication = new JwtAuthenticationToken("test");
		final JwtParser parser = new JwtParser(jwtAuthenticationProperties);
		final JwtAuthenticationProvider instance = new JwtAuthenticationProvider(parser);
		instance.retrieveUser("username", authentication);
	}

	/**
	 * Test of method that validates PersonTraits in the Jwt token, of class
	 * JwtAuthenticationProvider.
	 */
	@Test
	public void testIsPersonTraitsValid() {
		PersonTraits person = new PersonTraits();
		person.setFirstName("string");
		assertTrue(JwtAuthenticationProvider.isPersonTraitsValid(person, new String[] { "firstName" }));
		assertFalse(
				JwtAuthenticationProvider.isPersonTraitsValid(person, new String[] { "firstName", "assuranceLevel" }));
	}

	/**
	 * Test of method that validates PersonTraits in the Jwt token, of class
	 * JwtAuthenticationProvider.
	 */
	@Test
	public void testIsPersonTraitsValidInvocationException() {
		PersonTraits person = Mockito.mock(PersonTraits.class);
		when(person.getFirstName()).thenThrow(new IllegalArgumentException());
		person.setFirstName("string");
		assertFalse(JwtAuthenticationProvider.isPersonTraitsValid(person, new String[] { "firstName" }));
	}

	/**
	 * Test of method that validates PersonTraits in the Jwt token, of class
	 * JwtAuthenticationProvider.
	 */
	@Test
	public void testIsPersonTraitsValidWithInvalidJwtParameter() {
		PersonTraits person = new PersonTraits();
		person.setFirstName("string");
		assertFalse(JwtAuthenticationProvider.isPersonTraitsValid(person, new String[] { "testText" }));
	}

	/**
	 * Test of retrieveUser method for JwtAuthenticationException, of class
	 * JwtAuthenticationProvider.
	 */
	@Test(expected = JwtAuthenticationException.class)
	public void testRetrieveUserWithReqParamException() {
		final String username = "jdoe";
		final Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		person.setCorrelationIds(
				Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
						"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		final String token = tokenResource.getToken(person);

		final JwtAuthenticationToken authentication = new JwtAuthenticationToken(token);
		final JwtParser parser = new JwtParser(jwtAuthenticationProperties);
		final JwtAuthenticationProvider instance = new JwtAuthenticationProvider(parser);
		String[] jwtTokenRequiredParameterList = { "middleName" };
		ReflectionTestUtils.setField(instance, "jwtTokenRequiredParameterList", jwtTokenRequiredParameterList);
		final UserDetails result = instance.retrieveUser(username, authentication);
		assertNotNull(result);
	}
}
