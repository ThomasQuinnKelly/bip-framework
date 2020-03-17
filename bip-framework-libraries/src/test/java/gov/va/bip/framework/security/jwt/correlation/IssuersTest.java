package gov.va.bip.framework.security.jwt.correlation;

import gov.va.bip.framework.exception.BipRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssuersTest {

	@Test
	public final void testIssuers() {
		assertNotNull(Issuers.USDOD);
		assertNotNull(Issuers.USVBA);
		assertNotNull(Issuers.USVHA);
	}

	@Test
	public final void testValue() {
		assertTrue("USDOD".equals(Issuers.USDOD.value()));
		assertTrue("USVBA".equals(Issuers.USVBA.value()));
		assertTrue("USVHA".equals(Issuers.USVHA.value()));
	}

	@Test
	public final void testFromValue() {
		assertTrue(Issuers.fromValue("USDOD").equals(Issuers.USDOD));
		assertTrue(Issuers.fromValue("USVBA").equals(Issuers.USVBA));
		assertTrue(Issuers.fromValue("USVHA").equals(Issuers.USVHA));

		try {
			Issuers.fromValue("blah");
			fail("Should have thrown BipRuntimeException");
		} catch (Exception e) {
			assertTrue(BipRuntimeException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().startsWith("Issuer blah does not exist."));
		}
	}

}
