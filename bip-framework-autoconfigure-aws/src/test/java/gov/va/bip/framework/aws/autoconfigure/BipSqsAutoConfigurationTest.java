package gov.va.bip.framework.aws.autoconfigure;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class BipSqsAutoConfigurationTest {
	
	@Autowired
	BipSqsAutoConfiguration bipSqsAutoConfiguration;
	
	@Before
	public void setUp() throws Exception {
		bipSqsAutoConfiguration = new BipSqsAutoConfiguration();
	}

	@Test
	public void testSqsService(){
		assertNotNull(bipSqsAutoConfiguration.sqsService());
	}
	
}


