package gov.va.bip.framework.aws.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigConstantsTest {

	private static final String TEST_AWS_ID = "test-key";
	private static final String TEST_AWS_KEY = "test-secret";

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(TEST_AWS_ID, ConfigConstants.getAwsLocalstackId());
		Assert.assertEquals(TEST_AWS_KEY, ConfigConstants.getAwsLocalstackKey());
	}

}
