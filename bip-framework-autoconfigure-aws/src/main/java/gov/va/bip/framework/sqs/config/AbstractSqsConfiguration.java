package gov.va.bip.framework.sqs.config;

import cloud.localstack.Localstack;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import gov.va.bip.framework.localstack.autoconfigure.LocalstackAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@EnableJms
public abstract class AbstractSqsConfiguration {
	@Autowired
	Environment environment;

	@Value("${bip.framework.localstack.enabled:false}")
	boolean localstackEnabled;

	@SuppressWarnings("unused")
	@Autowired(required = false)
	private LocalstackAutoConfiguration localstackAutoConfiguration;

	boolean isEmbeddedAws = false;
	boolean isLocalInt = false;

	public abstract ConnectionFactory connectionFactory(SqsProperties sqsProperties);

	@Bean
	public DestinationResolver destinationResolver(SqsProperties sqsProperties) {

		return new StaticDestinationResolver(sqsProperties.getQueueName());
	}

	@Bean
	public JmsTemplate jmsTemplate(final SqsProperties sqsProperties, final ConnectionFactory connectionFactory) {

		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setDefaultDestinationName(sqsProperties.getQueueName());
		jmsTemplate.setSessionAcknowledgeMode(SQSSession.UNORDERED_ACKNOWLEDGE);
		jmsTemplate.setMessageTimestampEnabled(true);
		return jmsTemplate;
	}

	protected ConnectionFactory createStandardSQSConnectionFactory(final SqsProperties sqsProperties) {

		AmazonSQS sqsClient = createAmazonSQSClient(sqsProperties);

		ProviderConfiguration providerConfiguration = new ProviderConfiguration();
		sqsProperties.getPrefetch().ifPresent(providerConfiguration::setNumberOfMessagesToPrefetch);

		return new SQSConnectionFactory(providerConfiguration, sqsClient);
	}

	private AmazonSQS createAmazonSQSClient(final SqsProperties sqsProperties) {

		EndpointConfiguration endpointConfiguration = getEndpointConfiguration(sqsProperties);

		AWSCredentialsProvider awsCredentialsProvider =
				createAwsCredentialsProvider(sqsProperties.getAccessKey(), sqsProperties.getSecretKey());

		setProfiles();

		if (isEmbeddedAws || isLocalInt) {
			return AmazonSQSClientBuilder.standard().withCredentials(awsCredentialsProvider)
					.withEndpointConfiguration(endpointConfiguration).build();
		} else {
			return AmazonSQSClientBuilder.standard()
					.withEndpointConfiguration(endpointConfiguration).build();
		}
	}

	private void setProfiles() {
		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				isEmbeddedAws = true;
			}

			if (profileName.equals(BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT)) {
				isLocalInt = true;
			}
		}
	}

	private EndpointConfiguration getEndpointConfiguration(final SqsProperties sqsProperties) {
		EndpointConfiguration endpointConfiguration = null;

		Regions region = Regions.fromName(sqsProperties.getRegion());;

		if (localstackEnabled && isEmbeddedAws) {
			endpointConfiguration = new EndpointConfiguration(Localstack.INSTANCE.getEndpointSQS(), region.getName());

		} else if (isLocalInt) {
			if (sqsProperties.getSqsBaseUrl().contains("localhost")) {
				sqsProperties.setEndpoint(sqsProperties.getEndpoint().replace("localhost", "localstack"));
			}

			endpointConfiguration = new EndpointConfiguration(sqsProperties.getSqsBaseUrl(), region.getName());

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
