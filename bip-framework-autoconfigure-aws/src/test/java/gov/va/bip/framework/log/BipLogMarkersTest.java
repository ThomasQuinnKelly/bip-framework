package gov.va.bip.framework.log;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BipLogMarkersTest {

	@Test
	public final void testBipLogMarkers() {
		assertNotNull(BipLogMarkers.FATAL.getMarker());
		assertNotNull(BipLogMarkers.EXCEPTION.getMarker());
		assertNotNull(BipLogMarkers.TEST.getMarker());

		assertTrue("FATAL".equals(BipLogMarkers.FATAL.getMarker().getName()));
		assertTrue("EXCEPTION".equals(BipLogMarkers.EXCEPTION.getMarker().getName()));
		assertTrue("TEST".equals(BipLogMarkers.TEST.getMarker().getName()));
	}

}
