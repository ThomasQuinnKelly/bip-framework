package gov.va.bip.framework.log;

import org.junit.Test;
import org.slf4j.event.Level;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
