package gov.va.bip.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.event.Level;

import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

public class BipBaseLoggerTest {

	@Test
	public final void testGetSetLevel() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		Level level = logger.getLevel();
		assertNotNull(level);
		logger.setLevel(Level.INFO);
		assertTrue(Level.INFO.equals(logger.getLevel()));
		logger.info("Test message");
	}

}
