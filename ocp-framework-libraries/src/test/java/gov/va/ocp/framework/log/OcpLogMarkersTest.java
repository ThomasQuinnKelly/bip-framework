package gov.va.ocp.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.ocp.framework.log.OcpLogMarkers;

public class OcpLogMarkersTest {

	@Test
	public final void testReferenceLogMarkers() {
		assertNotNull(OcpLogMarkers.FATAL.getMarker());
		assertNotNull(OcpLogMarkers.EXCEPTION.getMarker());
		assertNotNull(OcpLogMarkers.TEST.getMarker());

		assertTrue("FATAL".equals(OcpLogMarkers.FATAL.getMarker().getName()));
		assertTrue("EXCEPTION".equals(OcpLogMarkers.EXCEPTION.getMarker().getName()));
		assertTrue("TEST".equals(OcpLogMarkers.TEST.getMarker().getName()));
	}

}
