package gov.va.bip.framework.security.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import gov.va.bip.framework.security.jwt.JwtAuthenticationException;
import gov.va.bip.framework.security.model.Person;
import gov.va.bip.framework.security.util.GenerateToken;

public class GenerateTokenTest {

	public GenerateTokenTest() {
	}

	/**
	 * Test of generateJwt method, of class GenerateToken.
	 */
	@Test
	public void testGenerateJwt_0args() {
		String expResult = "";
		String result = GenerateToken.generateJwt();
		assertNotNull(expResult, result);
	}

	/**
	 * Test of generateJwt method, of class GenerateToken.
	 */
	@Test
	public void testGenerateJwt_PersonTraits_String() {
		Person person = new Person();
		person.setCorrelationIds(Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		String secret = "test";
		String issuer = "Vets.gov";
		String result = GenerateToken.generateJwt(person, secret, issuer);
		assertNotNull(result);
	}

	/**
	 * Test of generateJwt method, of class GenerateToken.
	 */
	@Test
	public void testGenerateJwt_Person() {
		Person person = new Person();
		person.setCorrelationIds(Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		String result = GenerateToken.generateJwt(person);
		assertNotNull(result);
	}

	/**
	 * Test of generateJwt method, of class GenerateToken.
	 */
	@Test
	public void testGenerateJwt_int() {
		int expireInsec = 0;
		String result = GenerateToken.generateJwt(expireInsec);
		assertNotNull(result);
	}

	/**
	 * Test of generateJwt method, of class GenerateToken.
	 */
	@Test
	public void testGenerateJwt_4args() {
		Person person = new Person();
		person.setCorrelationIds(Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		int expireInsec = 10;
		String secret = "test";
		String issuer = "Vets.gov";
		String result = GenerateToken.generateJwt(person, expireInsec, secret, issuer, null);
		assertNotNull(result);
	}

	/**
	 * Test of generateJwt method for one of the possible negative scenarios based on the requiredParametersList(in this case the
	 * scenario has only one value - "pid"), of class GenerateToken.
	 */
	@Test
	public void testGenerateJwtForFailureOnNegativeScenario() {
		Person person = new Person();
		person.setCorrelationIds(Arrays.asList(new String[] { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"1040626995^NI^200DOD^USDOD^A", "796046489^SS" }));
		String secret = "test";
		String issuer = "Vets.gov";
		int expireInsec = 10;

		try {
			GenerateToken.generateJwt(person, expireInsec, secret, issuer, new String[] { "pid" });
			fail("gov.va.bip.framework.security.jwt.JwtAuthenticationException should be thrown");
		} catch (JwtAuthenticationException e) {
			assertTrue(e.getMessage().contains("Invalid Token"));
		}
	}

	/**
	 * Test of person method, of class GenerateToken.
	 */
	@Test
	public void testPerson() {
		Person result = GenerateToken.person();
		assertEquals("JANE", result.getFirstName());
	}

}
