package gov.va.bip.framework.log.logback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

public class BipMaskingMessageProviderTest {

	@Rule
	public OutputCapture capture = new OutputCapture();

	private static final BipLogger logger = BipLoggerFactory.getLogger(BipMaskingMessageProviderTest.class);

	@Test
	public void shouldMask() throws Exception {
		logger.info("This is a test with credit card number {}", "4111111111111111");
		assertThat(capture.toString()).contains("************1111").doesNotContain("4111111111111111");
	}

	@Test
	public void shouldContainStackTrace() throws Exception {
		logger.error("This is an error", new RuntimeException("Error!!"));
		DocumentContext out = JsonPath.parse(capture.toString());
		assertThat(out.read("$.severity", String.class)).isEqualTo("ERROR");
		assertThat(out.read("$.message", String.class)).contains("This is an error");
		assertThat(out.read("$.message", String.class)).contains("java.lang.RuntimeException: Error!!");
	}

}
