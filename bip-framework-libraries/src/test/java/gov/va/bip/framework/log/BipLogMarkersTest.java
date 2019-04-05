package gov.va.bip.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.bip.framework.log.BipLogMarkers;

public class BipLogMarkersTest {

	@Test
	public final void testReferenceLogMarkers() {
		assertNotNull(BipLogMarkers.FATAL.getMarker());
		assertNotNull(BipLogMarkers.EXCEPTION.getMarker());
		assertNotNull(BipLogMarkers.TEST.getMarker());

		assertTrue("FATAL".equals(BipLogMarkers.FATAL.getMarker().getName()));
		assertTrue("EXCEPTION".equals(BipLogMarkers.EXCEPTION.getMarker().getName()));
		assertTrue("TEST".equals(BipLogMarkers.TEST.getMarker().getName()));
	}

}
