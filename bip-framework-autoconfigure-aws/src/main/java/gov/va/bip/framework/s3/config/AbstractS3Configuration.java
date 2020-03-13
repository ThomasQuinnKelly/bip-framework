package gov.va.bip.framework.s3.config;

import cloud.localstack.Localstack;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import gov.va.bip.framework.config.AbstractAwsConfiguration;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public abstract class AbstractS3Configuration extends AbstractAwsConfiguration {

	@Bean
	public AmazonS3 amazonS3(final S3Properties s3Properties, Environment environment) {

		EndpointConfiguration endpointConfiguration = getEndpointConfiguration(s3Properties);

		AWSCredentialsProvider awsCredentialsProvider =
				createAwsCredentialsProvider(s3Properties.getAccessKey(), s3Properties.getSecretKey());

		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				setEmbeddedAws(true);
			}

			if (profileName.equals(BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT)) {
				setLocalInt(true);
			}
		}

		if (isEmbeddedAws() || isLocalInt()) {
			return AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider)
					.withEndpointConfiguration(endpointConfiguration).build();
		} else {
			return AmazonS3ClientBuilder.standard().withRegion("us-gov-west-1").build();
		}
	}

	private EndpointConfiguration getEndpointConfiguration(final S3Properties s3Properties) {

		EndpointConfiguration endpointConfiguration = null;

		Regions region = Regions.fromName(s3Properties.getRegion());

		if (isLocalstackEnabled() && isEmbeddedAws()) {
			endpointConfiguration =
					new EndpointConfiguration(Localstack.INSTANCE.getEndpointS3(), region.getName());
		} else if (isLocalInt()) {
			if (s3Properties.getBaseUrl().contains("localhost")) {
				s3Properties.setEndpoint(s3Properties.getEndpoint().replace("localhost", "localstack"));
			}
			endpointConfiguration = new EndpointConfiguration(s3Properties.getBaseUrl(), region.getName());
		}
		return endpointConfiguration;
	}

}
