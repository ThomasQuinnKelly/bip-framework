package gov.va.bip.framework.sns.config;

import cloud.localstack.Localstack;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import gov.va.bip.framework.config.AbstractAwsConfiguration;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(SnsProperties.class)
public abstract class AbstractSnsConfiguration extends AbstractAwsConfiguration {

	@Bean
	public AmazonSNS amazonSNS(final SnsProperties snsProperties, Environment environment) {

		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				isEmbeddedAws = true;
			}

			if (profileName.equals(BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT)) {
				isLocalInt = true;
			}
		}

		EndpointConfiguration endpointConfiguration = getEndpointConfiguration(snsProperties);

		AWSCredentialsProvider awsCredentialsProvider =
				createAwsCredentialsProvider(snsProperties.getAccessKey(), snsProperties.getSecretKey());

		if (isEmbeddedAws || isLocalInt) {
			return AmazonSNSClientBuilder.standard().withCredentials(awsCredentialsProvider)
					.withEndpointConfiguration(endpointConfiguration).build();
		} else {
			return AmazonSNSClientBuilder.standard().withRegion("us-gov-west-1").build();
		}
	}

	private EndpointConfiguration getEndpointConfiguration(final SnsProperties snsProperties) {

		EndpointConfiguration endpointConfiguration = null;

		Regions region = Regions.fromName(snsProperties.getRegion());

		if (localstackEnabled && isEmbeddedAws) {
			endpointConfiguration =
					new EndpointConfiguration(Localstack.INSTANCE.getEndpointSNS(), region.getName());
		} else if (isLocalInt) {
			if (snsProperties.getBaseUrl().contains("localhost")) {
				snsProperties.setEndpoint(snsProperties.getEndpoint().replace("localhost", "localstack"));
			}
			endpointConfiguration = new EndpointConfiguration(snsProperties.getBaseUrl(), region.getName());
		}
		return endpointConfiguration;
	}


}
