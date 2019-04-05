package gov.va.bip.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.event.Level;

import com.github.lalyos.jfiglet.FigletFont;

import gov.va.bip.framework.log.BipBanner;

public class BipBannerTest {

	private static final String TEXT = "TEST BANNER";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testReferenceLogBanner() throws IOException {
		String compare = "\n" + FigletFont.convertOneLine(BipBanner.FONT_FILE, Level.DEBUG.name() + ": " + TEXT.toUpperCase());
		BipBanner banner = new BipBanner(TEXT, Level.DEBUG);
		assertNotNull(banner);
		assertTrue(compare.equals(banner.getBanner()));
	}

	@Test
	public final void testReferenceLogBannerNullBannerText() throws IOException {
		String compare = "\n" + FigletFont.convertOneLine(BipBanner.FONT_FILE, Level.DEBUG.name() + ": ");
		BipBanner banner = new BipBanner(null, Level.DEBUG);
		assertNotNull(banner);
		assertTrue(compare.equals(banner.getBanner()));
	}

	@Test
	public final void testGetBannerLevel() throws IOException {
		// start with DEBUG
		BipBanner banner = new BipBanner(TEXT, Level.DEBUG);
		// verify same level
		String compare = "\n" + FigletFont.convertOneLine(BipBanner.FONT_FILE, Level.DEBUG.name() + ": " + TEXT.toUpperCase());
		String text = banner.getBanner(Level.DEBUG);
		assertNotNull(text);
		assertTrue(compare.equals(text));
	}

	@Test
	public final void testGetBannerLevelChanged() throws IOException {
		// start with DEBUG
		BipBanner banner = new BipBanner(TEXT, Level.DEBUG);
		// verify ERROR
		String compare = "\n" + FigletFont.convertOneLine(BipBanner.FONT_FILE, Level.ERROR.name() + ": " + TEXT.toUpperCase());
		String text = banner.getBanner(Level.ERROR);
		assertNotNull(text);
		assertTrue(compare.equals(text));
	}

	@Test
	public final void testGetLevel() throws IOException {
		BipBanner banner = new BipBanner(TEXT, Level.DEBUG);
		Level level = banner.getLevel();
		assertNotNull(level);
		assertTrue(Level.DEBUG.equals(level));
	}

//	@Test
//	public final void testGetBanner() {
//		fail("Not yet implemented");
//	}

}
