package gov.va.bip.framework.sns.config;

import cloud.localstack.Localstack;
import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import gov.va.bip.framework.localstack.autoconfigure.LocalstackAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(SnsProperties.class)
public abstract class AbstractSnsConfiguration {

	@Autowired
	Environment environment;

	@Value("${bip.framework.localstack.enabled:false}")
	boolean localstackEnabled;

	@SuppressWarnings("unused")
	@Autowired(required = false)
	private LocalstackAutoConfiguration localstackAutoConfiguration;

	private AmazonSNS createAmazonSNSClient(final SnsProperties snsProperties) {

		EndpointConfiguration endpointConfiguration = getEndpointConfiguration(snsProperties);

		AWSCredentialsProvider awsCredentialsProvider =
				createAwsCredentialsProvider(snsProperties.getAccessKey(), snsProperties.getSecretKey());

		return AmazonSNSClientBuilder.standard().withCredentials(awsCredentialsProvider)
				.withEndpointConfiguration(endpointConfiguration).build();
	}

	private EndpointConfiguration getEndpointConfiguration(final SnsProperties snsProperties) {
		EndpointConfiguration endpointConfiguration = null;

		Regions region = Regions.fromName(snsProperties.getRegion());

		if (localstackEnabled) {
			endpointConfiguration =
					new EndpointConfiguration(Localstack.INSTANCE.getEndpointSNS(), region.getName());
		} else {
			endpointConfiguration = new EndpointConfiguration(snsProperties.getEndpoint(), region.getName());
		}
		return endpointConfiguration;
	}

	private AWSCredentialsProvider createAwsCredentialsProvider(final String localAccessKey, final String localSecretKey) {

		AWSCredentialsProvider ec2ContainerCredentialsProvider = new EC2ContainerCredentialsProviderWrapper();

		if (StringUtils.isEmpty(localAccessKey) || StringUtils.isEmpty(localSecretKey)) {
			return ec2ContainerCredentialsProvider;
		}

		AWSCredentialsProvider localAwsCredentialsProvider =
				new AWSStaticCredentialsProvider(new BasicAWSCredentials(localAccessKey, localSecretKey));

		return new AWSCredentialsProviderChain(localAwsCredentialsProvider, ec2ContainerCredentialsProvider);
	}


}
