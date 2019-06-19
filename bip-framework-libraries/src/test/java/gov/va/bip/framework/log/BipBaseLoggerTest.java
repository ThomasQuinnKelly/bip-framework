package gov.va.bip.framework.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.event.Level;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.AbstractBaseLogTester;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

public class BipBaseLoggerTest extends AbstractBaseLogTester {

	public static final int MAX_MSG_LENGTH = 6144;

	@Test
	public void testGetSetLevel() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		Level level = logger.getLevel();
		assertNotNull(level);
		logger.setLevel(Level.INFO);
		assertTrue(Level.INFO.equals(logger.getLevel()));
		logger.info("Test message");
	}

	@Test
	public void testMakeToLength_firstWordIsBiggerThanMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength", StringUtils.repeat("a", MAX_MSG_LENGTH) + "extraSuffix",
				listOfLogMessages,
				MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(1).equals("extraSuffix "));
	}

	@Test
	public void testMakeToLength_firstWordIsBiggerThan2TimesMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength",
				StringUtils.repeat("a", MAX_MSG_LENGTH * 2) + "extraSuffix plus a few more words", listOfLogMessages, MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(1).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(2).equals("extraSuffix plus a few more words "));
	}

	@Test
	public void testMakeToLength_thirdWordIsBiggerThanMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength",
				"few words " + StringUtils.repeat("a", MAX_MSG_LENGTH) + "extraSuffix plus a few more words", listOfLogMessages,
				MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals("few words "));
		assertTrue(listOfLogMessages.get(1).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(2).equals("extraSuffix plus a few more words "));
	}

	@Test
	public void testMakeToLength_thirdWordIsBiggerThan2TimesMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength",
				"few words " + StringUtils.repeat("a", MAX_MSG_LENGTH * 2) + "extraSuffix plus a few more words", listOfLogMessages,
				MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals("few words "));
		assertTrue(listOfLogMessages.get(1).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(2).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(3).equals("extraSuffix plus a few more words "));
	}

	@Test
	public void testMakeToLength_LastWordIsBiggerThanMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength",
				"words before last word " + StringUtils.repeat("a", MAX_MSG_LENGTH) + "extraSuffix", listOfLogMessages,
				MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals("words before last word "));
		assertTrue(listOfLogMessages.get(1).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(2).equals("extraSuffix "));
	}

	@Test
	public void testMakeToLength_LastWordIsBiggerThan2TimesMaxLength() {
		BipLogger logger = BipLoggerFactory.getLogger(BipBanner.class);
		LinkedList<String> listOfLogMessages = new LinkedList<String>();
		ReflectionTestUtils.invokeMethod(logger, "makeToLength",
				"words before last word " + StringUtils.repeat("a", MAX_MSG_LENGTH * 2) + "extraSuffix", listOfLogMessages,
				MAX_MSG_LENGTH);
		assertTrue(listOfLogMessages.get(0).equals("words before last word "));
		assertTrue(listOfLogMessages.get(1).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(2).equals(StringUtils.repeat("a", MAX_MSG_LENGTH)));
		assertTrue(listOfLogMessages.get(3).equals("extraSuffix "));
	}
}
