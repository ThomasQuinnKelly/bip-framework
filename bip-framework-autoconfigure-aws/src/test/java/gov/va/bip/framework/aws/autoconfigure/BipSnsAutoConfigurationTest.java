package gov.va.bip.framework.aws.autoconfigure;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class BipSnsAutoConfigurationTest {
	
	@Autowired
	BipSnsAutoConfiguration bipSnsAutoConfiguration;
	
	@Before
	public void setUp() throws Exception {
		bipSnsAutoConfiguration = new BipSnsAutoConfiguration();
	}

	@Test
	public void testSnsService(){
		assertNotNull(bipSnsAutoConfiguration.snsService());
	}
	
}


