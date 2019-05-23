package gov.va.bip.framework.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.va.bip.framework.cache.BipCacheUtil;
import gov.va.bip.framework.security.PersonTraits;
import gov.va.bip.framework.service.DomainResponse;

public class BipCacheUtilTest {

	@After
	public void teardown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testCheckResultConditions() {
		DomainResponse domainResponse = new DomainResponse();
		domainResponse.setDoNotCacheResponse(true);
		boolean result = BipCacheUtil.checkResultConditions(domainResponse);
		assertTrue(result);
		domainResponse.setDoNotCacheResponse(false);
		result = BipCacheUtil.checkResultConditions(domainResponse);
		assertFalse(result);
	}

	@Test
	public void testGetUserBasedKey() {
		PersonTraits personTraits = new PersonTraits("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_TEST"));
		personTraits.setPid("12345");
		personTraits.setFirstName("firstName");
		personTraits.setLastName("lastName");
		Authentication auth = new UsernamePasswordAuthenticationToken(personTraits,
				personTraits.getPassword(), personTraits.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}

	@Test
	public void testGetUserBasedKey_fileNumber() {
		PersonTraits personTraits = new PersonTraits("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_TEST"));
		personTraits.setFileNumber("12345");
		personTraits.setFirstName("firstName");
		personTraits.setLastName("lastName");
		Authentication auth = new UsernamePasswordAuthenticationToken(personTraits,
				personTraits.getPassword(), personTraits.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}

	@Test
	public void testGetUserBasedKey_pidZeroLength() {
		PersonTraits personTraits = new PersonTraits("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_TEST"));
		personTraits.setPid("");
		personTraits.setFirstName("firstName");
		personTraits.setLastName("lastName");
		Authentication auth = new UsernamePasswordAuthenticationToken(personTraits,
				personTraits.getPassword(), personTraits.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}

	@Test
	public void testGetUserBasedKey_fileNumberZeroLength() {
		PersonTraits personTraits = new PersonTraits("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_TEST"));
		personTraits.setFileNumber("");
		personTraits.setFirstName("firstName");
		personTraits.setLastName("lastName");
		Authentication auth = new UsernamePasswordAuthenticationToken(personTraits,
				personTraits.getPassword(), personTraits.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}

	@Test
	public void testGetUserBasedKey_fileNumberAndPidNull() {
		PersonTraits personTraits = new PersonTraits("user", "password",
				AuthorityUtils.createAuthorityList("ROLE_TEST"));
		personTraits.setFirstName("firstName");
		personTraits.setLastName("lastName");
		Authentication auth = new UsernamePasswordAuthenticationToken(personTraits,
				personTraits.getPassword(), personTraits.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}

	@Test
	public void testGetUserBasedKeyForNull() {
		SecurityContextHolder.getContext().setAuthentication(null);
		String result = BipCacheUtil.getUserBasedKey("test");
		assertTrue(result.length() > 0);
	}
}
