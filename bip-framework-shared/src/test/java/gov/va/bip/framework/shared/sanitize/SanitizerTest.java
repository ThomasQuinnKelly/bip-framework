package gov.va.bip.framework.shared.sanitize;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import gov.va.bip.framework.shared.sanitize.Sanitizer;

/**
 * Sanitization utility Test class
 */
public class SanitizerTest {

	@Test
	public void testStripXss() {
		String str = "test string";
		assertTrue(str.equals(Sanitizer.stripXss(str)));

		str = "test \nstring";
		assertTrue("test \nstring".equals(Sanitizer.stripXss(str)));

		str = null;
		assertNull(Sanitizer.stripXss(str));

		str = "javascript:alert('Hello')";
		assertTrue("alert('Hello')".equals(Sanitizer.stripXss(str)));

		str = "<script>alert('Hello')<script>";
		assertTrue("alert('Hello')".equals(Sanitizer.stripXss(str)));

		str = "</script>";
		assertTrue("".equals(Sanitizer.stripXss(str)));
	}

	@Test
	public void testSafePath() {
		final String goodPath = "testdot.testcolon:testslash/testbackslash\\test";
		final String safeGoodPath = Sanitizer.safePath(goodPath);
		assertTrue(goodPath.equals(safeGoodPath));

		StringBuilder badcharPath = new StringBuilder();
		badcharPath.append(".").append((char) 63).append("badCharWasBeforeThis:");
		final String safeBadcharPath = Sanitizer.safePath(badcharPath.toString());
		assertTrue(".badCharWasBeforeThis:".equals(safeBadcharPath));
	}

	@Test
	public void testSafePathWithNullPath() {
		assertNull(Sanitizer.safePath(null));
	}

	@Test
	public void testSafeFilename() {

		// copied from the method under test
		final int[] illegalChars = { 34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47 };

		final StringBuilder illegalString = new StringBuilder();
		for (int i = 0; i < illegalChars.length; i++) {
			illegalString.append((char) illegalChars[i]);
		}

		final String filename = "Test " + illegalString.toString() + " file.name";

		final String safe = Sanitizer.safeFilename(filename);

		assertTrue("Test  file.name".equals(safe));
	}

	@Test
	public void testSafeFilename_withNullFilename() {
		assertNull(Sanitizer.safeFilename(null));
	}
}
