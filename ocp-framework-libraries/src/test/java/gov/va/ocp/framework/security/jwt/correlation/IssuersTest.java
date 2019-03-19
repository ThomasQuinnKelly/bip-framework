package gov.va.ocp.framework.security.jwt.correlation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.ocp.framework.exception.OcpRuntimeException;

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
			fail("Should have thrown OcpRuntimeException");
		} catch (Exception e) {
			assertTrue(OcpRuntimeException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().startsWith("Issuer {} does not exist:"));
		}
	}

}
