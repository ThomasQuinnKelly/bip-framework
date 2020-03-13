package gov.va.bip.framework.s3.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Region;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.assertNotNull;

public class AbstractS3ConfigurationTest {

	@Test
	public void testCreateAmazonS3() {
		final S3Properties s3Properties = new S3Properties();
		s3Properties.setEndpoint("http://localhost:8080/endpoint");
		s3Properties.setRegion(Region.US_West.toString());

		final AbstractS3Configuration instance = new AbstractS3ConfigurationImpl();
		final Environment env = new MockEnvironment();

		final AmazonS3 amazonS3 = instance.amazonS3(s3Properties, env);
		assertNotNull(amazonS3);
	}

	@Test
	public void testCreateAmazonS3_4() {
		final S3Properties s3Properties = new S3Properties();
		s3Properties.setEndpoint("http://localhost:8080/endpoint");
		s3Properties.setRegion(Region.US_West.toString());

		final AbstractS3Configuration instance = new AbstractS3ConfigurationImpl();
		final Environment env = new MockEnvironment();

		instance.setLocalInt(true);

		final AmazonS3 amazonS3 = instance.amazonS3(s3Properties, env);
		assertNotNull(amazonS3);
	}

	public class AbstractS3ConfigurationImpl extends AbstractS3Configuration {

	}

}
