package gov.va.bip.framework.aws.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigConstantsTest {


	private static final String TEST_AWS_ID = "test-key";
	private static final String TEST_AWS_KEY = "test-secret";

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(TEST_AWS_ID, ConfigConstants.aws_credentials.AWS_LOCALSTACK_ID.toString());
		Assert.assertEquals(TEST_AWS_KEY, ConfigConstants.aws_credentials.AWS_LOCALSTACK_KEY.toString());
	}

}
