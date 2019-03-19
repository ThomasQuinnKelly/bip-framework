package gov.va.ocp.framework.security.jwt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ocp.framework.security.PersonTraits;
import gov.va.ocp.framework.security.config.OcpSecurityTestConfig;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.ocp.framework.security.jwt.JwtParser;
import gov.va.ocp.framework.security.model.Person;
import gov.va.ocp.framework.security.util.GenerateToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcpSecurityTestConfig.class)
public class JwtParserTest {

	private static final Date BIRTH_DATE = Calendar.getInstance().getTime();

	private String token;

	@Autowired
	private JwtAuthenticationProperties jwtAuthenticationProperties;

	private JwtParser jwtParser;

	SimpleDateFormat format1 = new SimpleDateFormat(PersonTraits.PATTERN_FORMAT.BIRTHDATE_YYYYMMDD.getPattern());

	@Before
	public void setup() {
		jwtParser = new JwtParser(jwtAuthenticationProperties);
		Person person = new Person();
		person.setFirstName("FN");
		person.setLastName("LN");
		person.setMiddleName("MN");
		person.setPrefix("Dr.");
		person.setSuffix("Jr");

		person.setBirthDate(format1.format(BIRTH_DATE));
		person.setAssuranceLevel(2);
		person.setEmail("validemail@testdomain.com");
		person.setGender("M");

		token = GenerateToken.generateJwt(person, jwtAuthenticationProperties.getExpireInSeconds(),
				jwtAuthenticationProperties.getSecret(), jwtAuthenticationProperties.getIssuer(), new String[] {});
	}

	@Test
	public void parseJwtTest() {
		PersonTraits personTraits = jwtParser.parseJwt(token);
		assertNotNull(personTraits.getFirstName());
		assertTrue("FN".equals(personTraits.getFirstName()));
		assertNotNull(personTraits.getLastName());
		assertTrue("LN".equals(personTraits.getLastName()));
		assertNotNull(personTraits.getMiddleName());
		assertTrue("MN".equals(personTraits.getMiddleName()));
		assertNotNull(personTraits.getPrefix());
		assertTrue("Dr.".equals(personTraits.getPrefix()));
		assertNotNull(personTraits.getSuffix());
		assertTrue("Jr".equals(personTraits.getSuffix()));
		assertNotNull(personTraits.getBirthDate());
		assertTrue(format1.format(BIRTH_DATE).equals(personTraits.getBirthDate()));
		assertNotNull(personTraits.getGender());
		assertTrue("M".equals(personTraits.getGender()));
		assertNotNull(personTraits.getAssuranceLevel());
		assertTrue(personTraits.getAssuranceLevel().equals(2));
		assertNotNull(personTraits.getEmail());
		assertTrue("validemail@testdomain.com".equals(personTraits.getEmail()));
		assertNotNull(personTraits.getUser());
	}

}
