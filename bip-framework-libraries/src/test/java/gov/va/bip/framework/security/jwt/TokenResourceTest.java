package gov.va.bip.framework.security.jwt;

import gov.va.bip.framework.security.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.WebDataBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class TokenResourceTest {

	/**
	 * Test of getToken method, of class TokenResource.
	 */
	@Test
	public void testGetToken() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}
	
	/**
	 * Test of getToken method, of class TokenResource.
	 * 
	 * To test JwtKeyPairs with valid secret and issuer
	 */
	@Test
	public void testGetTokenValidJwtKeyPair() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		List<JwtAuthenticationProperties.JwtKeyPairs> listKeyPairs = new ArrayList<JwtAuthenticationProperties.JwtKeyPairs>();
		listKeyPairs.add(new JwtAuthenticationProperties.JwtKeyPairs("secret","Vets.gov"));
		jwtAuthenticationProperties.setKeyPairs(listKeyPairs);
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}
	
	/**
	 * Test of getToken method, of class TokenResource.
	 * 
	 * To test JwtKeyPairs as null
	 */
	@Test
	public void testGetTokenNullJwtKeyPair() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		jwtAuthenticationProperties.setKeyPairs(null);
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}
	
	/**
	 * Test of getToken method, of class TokenResource.
	 * 
	 * To test JwtKeyPairs with null element in the key pair
	 */
	@Test
	public void testGetTokenNullElementJwtKeyPair() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		List<JwtAuthenticationProperties.JwtKeyPairs> listKeyPairs = new ArrayList<JwtAuthenticationProperties.JwtKeyPairs>();
		listKeyPairs.add(null);
		jwtAuthenticationProperties.setKeyPairs(listKeyPairs);
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}
	
	/**
	 * Test of getToken method, of class TokenResource.
	 * 
	 * To test JwtKeyPairs with null secret and valid issuer
	 */
	@Test
	public void testGetTokenNullSecretJwtKeyPair() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		List<JwtAuthenticationProperties.JwtKeyPairs> listKeyPairs = new ArrayList<JwtAuthenticationProperties.JwtKeyPairs>();
		listKeyPairs.add(new JwtAuthenticationProperties.JwtKeyPairs(null,"Vets.gov"));
		jwtAuthenticationProperties.setKeyPairs(listKeyPairs);
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}
	
	/**
	 * Test of getToken method, of class TokenResource.
	 * 
	 * To test JwtKeyPairs with valid secret and null issuer
	 */
	@Test
	public void testGetTokenNullIssuerJwtKeyPair() {
		TokenResource tokenResource = new TokenResource();
		JwtAuthenticationProperties jwtAuthenticationProperties = new JwtAuthenticationProperties();
		List<JwtAuthenticationProperties.JwtKeyPairs> listKeyPairs = new ArrayList<JwtAuthenticationProperties.JwtKeyPairs>();
		listKeyPairs.add(new JwtAuthenticationProperties.JwtKeyPairs("secret",null));
		jwtAuthenticationProperties.setKeyPairs(listKeyPairs);
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
		ReflectionTestUtils.setField(tokenResource, "jwtAuthenticationProperties", jwtAuthenticationProperties);
		String result = tokenResource.getToken(person);
		assertTrue(result.length() > 0);

	}

	/**
	 * Test of initBinder method, of class TokenResource.
	 */
	@Test
	public void testInitBinder() {
		WebDataBinder binder = new WebDataBinder(null, null);
		TokenResource instance = new TokenResource();
		instance.initBinder(binder);
		assertTrue(binder.getAllowedFields().length > 0);
	}
}
