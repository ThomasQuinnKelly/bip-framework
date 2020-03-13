package gov.va.bip.framework.sqs.config;

import cloud.localstack.Localstack;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import gov.va.bip.framework.config.AbstractAwsConfiguration;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.ConnectionFactory;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@EnableJms
public abstract class AbstractSqsConfiguration extends AbstractAwsConfiguration {
	@Autowired
	Environment environment;

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

		setProfiles();

		EndpointConfiguration endpointConfiguration = getEndpointConfiguration(sqsProperties);

		AWSCredentialsProvider awsCredentialsProvider =
				createAwsCredentialsProvider(sqsProperties.getAccessKey(), sqsProperties.getSecretKey());

		if (isEmbeddedAws() || isLocalInt()) {
			return AmazonSQSClientBuilder.standard().withCredentials(awsCredentialsProvider)
					.withEndpointConfiguration(endpointConfiguration).build();
		} else {
			return AmazonSQSClientBuilder.standard().withRegion("us-gov-west-1").build();
		}
	}

	private void setProfiles() {
		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				setEmbeddedAws(true);
			}

			if (profileName.equals(BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT)) {
				setLocalInt(true);
			}
		}
	}

	private EndpointConfiguration getEndpointConfiguration(final SqsProperties sqsProperties) {
		EndpointConfiguration endpointConfiguration = null;

		Regions region = Regions.fromName(sqsProperties.getRegion());

		if (isLocalstackEnabled() && isEmbeddedAws()) {
			endpointConfiguration = new EndpointConfiguration(Localstack.INSTANCE.getEndpointSQS(), region.getName());

		} else if (isLocalInt()) {
			if (sqsProperties.getBaseUrl().contains("localhost")) {
				sqsProperties.setEndpoint(sqsProperties.getEndpoint().replace("localhost", "localstack"));
			}
			endpointConfiguration = new EndpointConfiguration(sqsProperties.getBaseUrl(), region.getName());
		}
		return endpointConfiguration;
	}

}
