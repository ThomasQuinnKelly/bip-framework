package gov.va.bip.framework.security.jwt.correlation;

import gov.va.bip.framework.exception.BipRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdTypesTest {

	@Test
	public final void testIdTypes() {
		assertNotNull(IdTypes.NATIONAL);
		assertNotNull(IdTypes.PATIENT);
		assertNotNull(IdTypes.SOCIAL);
	}

	@Test
	public final void testValue() {
		assertTrue("NI".equals(IdTypes.NATIONAL.value()));
		assertTrue("PI".equals(IdTypes.PATIENT.value()));
		assertTrue("SS".equals(IdTypes.SOCIAL.value()));
	}

	@Test
	public final void testFromValue() {
		assertTrue(IdTypes.fromValue("NI").equals(IdTypes.NATIONAL));
		assertTrue(IdTypes.fromValue("PI").equals(IdTypes.PATIENT));
		assertTrue(IdTypes.fromValue("SS").equals(IdTypes.SOCIAL));

		try {
			IdTypes.fromValue("XX");
			fail("Should have thrown BipRuntimeException");
		} catch (Exception e) {
			assertTrue(BipRuntimeException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().startsWith("IdType XX does not exist."));
		}
	}

}
