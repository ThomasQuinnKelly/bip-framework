package gov.va.bip.framework.security.jwt.correlation;

import gov.va.bip.framework.exception.BipRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class SourcesTest {

	@Test
	public final void testSources() {
		assertNotNull(Sources.BIRLS);
		assertNotNull(Sources.CORP);
		assertNotNull(Sources.ICN);
		assertNotNull(Sources.USDOD);
	}

	@Test
	public final void testValue() {
		assertTrue("200M".equals(Sources.ICN.value()));
		assertTrue("200DOD".equals(Sources.USDOD.value()));
		assertTrue("200BRLS".equals(Sources.BIRLS.value()));
		assertTrue("200CORP".equals(Sources.CORP.value()));
	}

	@Test
	public final void testFromValue() {
		assertTrue(Sources.fromValue("200M").equals(Sources.ICN));
		assertTrue(Sources.fromValue("200DOD").equals(Sources.USDOD));
		assertTrue(Sources.fromValue("200BRLS").equals(Sources.BIRLS));
		assertTrue(Sources.fromValue("200CORP").equals(Sources.CORP));

		try {
			Sources.fromValue("blah");
			fail("Should have thrown BipRuntimeException");
		} catch (Exception e) {
			assertTrue(BipRuntimeException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().startsWith("Source blah does not exist."));
		}
	}

}
