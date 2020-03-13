package gov.va.bip.framework.sns.config;

import com.amazonaws.services.sns.AmazonSNS;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.assertNotNull;

public class AbstractSnsConfigurationTest {

	@Test
	public void testCreateAmazonSNS() {
		final SnsProperties snsProperties = new SnsProperties();
		snsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		snsProperties.setRegion("us-west-2");

		final AbstractSnsConfiguration instance = new AbstractSnsConfigurationImpl();
		final Environment env = new MockEnvironment();

		final AmazonSNS amazonSNS = instance.amazonSNS(snsProperties, env);
		assertNotNull(amazonSNS);
	}

	public class AbstractSnsConfigurationImpl extends AbstractSnsConfiguration {

	}

}
