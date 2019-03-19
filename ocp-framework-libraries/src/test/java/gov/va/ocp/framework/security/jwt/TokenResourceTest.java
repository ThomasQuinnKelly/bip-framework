package gov.va.ocp.framework.security.jwt;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.WebDataBinder;

import gov.va.ocp.framework.security.config.OcpSecurityTestConfig;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.ocp.framework.security.jwt.TokenResource;
import gov.va.ocp.framework.security.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcpSecurityTestConfig.class)
public class TokenResourceTest {

	@Autowired
	TokenResource tokenResource;

	@Autowired
	JwtAuthenticationProperties properties;

	@Autowired
	AuthenticationProvider provider;

	/**
	 * Test of getToken method, of class TokenResource.
	 */
	@Test
	public void testGetToken() {
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");
		String[] arrayOfCorrelationIds = { "1012832469V956223^NI^200M^USVHA^P", "796046489^PI^200BRLS^USVBA^A",
				"600071516^PI^200CORP^USVBA^A", "1040626995^NI^200DOD^USDOD^A", "796046489^SS" };
		person.setCorrelationIds(Arrays.asList(arrayOfCorrelationIds));
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
