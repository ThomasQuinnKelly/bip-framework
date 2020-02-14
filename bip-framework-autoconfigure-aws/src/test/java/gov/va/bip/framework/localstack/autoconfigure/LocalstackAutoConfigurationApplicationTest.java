package gov.va.bip.framework.localstack.autoconfigure;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class LocalstackAutoConfigurationApplicationTest {

	@Autowired
	LocalstackAutoConfiguration localstackAutoConfiguration;


	@Test
	public void testStartStack() throws Exception {
		localstackAutoConfiguration.startAwsLocalStack();
	}

	@Test
	public void testStopStack() throws Exception {
		localstackAutoConfiguration.stopAwsLocalStack();
	}

	@Test
	public void testProfileCheck() throws Exception {
		localstackAutoConfiguration.profileCheck();
	}
}
