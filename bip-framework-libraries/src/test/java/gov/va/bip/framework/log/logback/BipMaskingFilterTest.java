package gov.va.bip.framework.log.logback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;

@RunWith(SpringRunner.class)
public class BipMaskingFilterTest {

	Logger logger = LoggerFactory.getLogger(BipMaskingFilterTest.class);

	@Rule
	public OutputCapture capture = new OutputCapture();

	@Test
	public final void testEvaluate_NoMasking() {
		String msg = "TEST simple message";
		logger.error(msg);
		String log = capture.toString();
		assertTrue(log.contains(msg));
	}

	@Test
	public final void testEvaluate_MaskNumber() {
		String msg = "Test credit card 12345678901234 value";
		logger.error(msg);
		String log = capture.toString();
		assertTrue(log.contains("Test credit card **********1234 value"));
	}

	@Test
	public final void testEvaluate_SsnAndFileNumber() {
		String msg = "Test SSN 123-45-6789 value";
		logger.error(msg);
		String log = capture.toString();
		assertTrue(log.contains("Test SSN *******6789 value"));
	}

	@Test
	public final void testEvaluate_FilterPattern() {
		String msg = "Test sample filter 123-456 value";
		logger.error(msg);
		String log = capture.toString();
		assertTrue(log.contains("Test sample filter ****456 value"));
	}

	@Test
	public final void testEvaluate_negative() {
		BipMaskingFilter testFilter = new BipMaskingFilter();
		LoggingEvent event = new LoggingEvent();

		try {
			// blank pattern
			assertFalse(testFilter.evaluate(event));

			testFilter.setPattern("TEST");

			// with args
			event.setArgumentArray(new Object[] { "arg1" });
			event.setMessage("This is a test");
			assertTrue(testFilter.evaluate(event));

			// with prefix, suffix, and parens around pattern
			testFilter.setPrefix("A");
			testFilter.setPrefix("Z");
			testFilter.setPattern("(" + testFilter.getPattern() + ")");
			assertTrue(testFilter.evaluate(event));
		} catch (EvaluationException e) {
			fail("Should not have thrown exception");
		}

		// throw bogus exception
		BipMaskingFilter spiedFilter = Mockito.spy(testFilter);
		Mockito.doThrow(new RuntimeException("Intentionally thrown")).when(spiedFilter)
				.updateMessage(org.mockito.ArgumentMatchers.any(ILoggingEvent.class), org.mockito.ArgumentMatchers.any(String.class));
		try {
			testFilter.evaluate(event);
		} catch (EvaluationException e) {
			assertTrue("Intentionally thrown".equals(e.getCause().getMessage()));
		}
	}

	@Test
	public final void testHashCodeAndEqualsAndEtters()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		BipMaskingFilter testFilter = new BipMaskingFilter();
		BipMaskingFilter otherFilter = new BipMaskingFilter();

		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(testFilter));
		assertFalse(testFilter.equals(null));
		assertFalse(testFilter.equals("A different type"));
		assertTrue(testFilter.equals(otherFilter));

		// get field from filter superclass
		Field name = ReflectionUtils.findField(BipMaskingFilter.class, "name");
		name.setAccessible(true);

		name.set(testFilter, "TEST");
		assertTrue(testFilter.hashCode() != 0);

		assertFalse(testFilter.equals(otherFilter));
		name.set(otherFilter, "TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(otherFilter));
		name.set(testFilter, null);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		name.set(testFilter, "TEST"); // for next test

		testFilter.setPattern("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		otherFilter.setPattern("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(otherFilter));
		testFilter.setPattern(null);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		testFilter.setPattern("TEST"); // for next test

		testFilter.setPrefix("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		otherFilter.setPrefix("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(otherFilter));
		testFilter.setPrefix(null);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		testFilter.setPrefix("TEST"); // for next test

		testFilter.setSuffix("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		otherFilter.setSuffix("TEST");
		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(otherFilter));
		testFilter.setSuffix(null);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		testFilter.setSuffix("TEST"); // for next test

		testFilter.setUnmasked(4);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
		otherFilter.setUnmasked(4);
		assertTrue(testFilter.hashCode() != 0);
		assertTrue(testFilter.equals(otherFilter));
		testFilter.setUnmasked(0);
		assertTrue(testFilter.hashCode() != 0);
		assertFalse(testFilter.equals(otherFilter));
	}

	@Test
	public final void testToString() {
		BipMaskingFilter testFilter = new BipMaskingFilter();
		assertTrue(testFilter.toString().length() > 0);
	}
}
