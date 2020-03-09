package gov.va.bip.framework.localstack.autoconfigure;

import cloud.localstack.Localstack;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.annotation.LocalstackDockerConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import gov.va.bip.framework.s3.config.S3Properties;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sqs.config.SqsProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Performs configuration of a Localstack instance
 *
 */
@Configuration
@Profile({BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS, BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT})
@EnableConfigurationProperties({ LocalstackProperties.class})
@ConditionalOnProperty(name = "bip.framework.localstack.enabled", havingValue = "true")
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocalstackAutoConfiguration {
	/**
	 * Class logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalstackAutoConfiguration.class);
	private static final String RETRY_MESSAGE = ") failed on try # ";
	private static final String WAIT_FOR_LOCALSTACK_MESSAGE = ", waiting for AWS localstack to finish initializing.";
	private static final String LOCALHOST = "localhost";
	private static final String LOCALSTACK = "localstack";
	

	@Autowired
	private LocalstackProperties localstackProperties;

	@Autowired
	private SqsProperties sqsProperties;

	@Autowired
	private SnsProperties snsProperties;

	@Autowired
	private S3Properties s3Properties;

	@Autowired
	Environment environment;

	private static Integer maxRetries = 60;

	@Value("${bip.framework.localstack.externalHostName:localhost}")
	private String externalHostName;

	@Value("${bip.framework.localstack.imageTag:0.10.7}")
	private String imageTag;

	@Value("${bip.framework.localstack.pullNewImage:false}")
	private boolean pullNewImage;

	@Value("${bip.framework.localstack.randomizePorts:false}")
	private boolean randomizePorts;

	private Map<String, String> environmentVariables = new HashMap<>();

	//Initialize Queue Variables
	String dlqUrl = null;

	public boolean profileCheck(String profile) {
		boolean profileMatches = false;

		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(profile)) {
				profileMatches = true;
			}
		}
		return profileMatches;

	}

	/**
	 * Start embedded AWS servers on context load
	 *
	 * @throws IOException
	 */
	@PostConstruct
	public void startAwsLocalStack() {
		if (profileCheck(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {

			if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
				LOGGER.info("Localstack instance is running...");
			} else if (Localstack.INSTANCE != null) {
				// Clean the localstack
				cleanAwsLocalStack();

				Localstack.INSTANCE.startup(buildLocalstackDockerConfiguration());

			}
		}

		//Create Localstack Services
		createLocalstackServices();
	}


	private LocalstackDockerConfiguration buildLocalstackDockerConfiguration() {
		LocalstackDockerConfiguration.LocalstackDockerConfigurationBuilder configBuilder = LocalstackDockerConfiguration.builder();

		configBuilder.externalHostName(externalHostName);
		configBuilder.imageTag(imageTag);
		configBuilder.pullNewImage(pullNewImage);
		configBuilder.randomizePorts(randomizePorts);

		List<LocalstackProperties.Services> listServices = localstackProperties.getServices();

		if (!CollectionUtils.isEmpty(listServices)) {
			// Put selected services into a list
			StringBuilder builder = new StringBuilder();
			for (LocalstackProperties.Services service : listServices) {
				builder.append(service.getName());
				builder.append(":");
				builder.append(service.getPort());
				builder.append(",");
			}

			// Remove last delimiter with setLength.
			builder.setLength(builder.length() - 1);

			String services = String.join(",", builder.toString());
			if (StringUtils.isNotEmpty(services)) {
				// Listed Services will be started
				environmentVariables.put("SERVICES", services);
			}

			configBuilder.environmentVariables(environmentVariables);

			// You can set the ports if needed using properties for each service
			configBuilder.randomizePorts(false);
		}

		return configBuilder.build();
	}

	/**
	 * Stop embedded AWS servers on context destroy
	 */
	@PreDestroy
	public void stopAwsLocalStack() {
		if (profileCheck(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
			// Stop the localstack
			if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
				Localstack.INSTANCE.stop();
			}

			// Clean the localstack
			cleanAwsLocalStack();
		}
	}

	/**
	 * Clean AWS Localstack containers
	 */
	private void cleanAwsLocalStack() {
		// Get containers Ids
		DockerExe newDockerExe = new DockerExe();
		String listContainerIds =
				newDockerExe.execute(Arrays.asList("ps", "--no-trunc", "-aq", "--filter", "ancestor=localstack/localstack:" + imageTag));

		// Clean up docker containers
		if (StringUtils.isNotEmpty(listContainerIds)) {
			try {
				String[] splitArray = listContainerIds.split("\\s+");
				for (String containerId : splitArray) {
					newDockerExe.execute(Arrays.asList("rm", "-f", containerId));
				}
			} catch (PatternSyntaxException ex) {
				// PatternSyntaxException During Splitting
			}
		}
	}

	private CreateTopicResult createTopics(AmazonSNS client) {
		// retry the operation until the localstack responds
		for (int i = 0; i < maxRetries; i++) {
			try {
				return client.createTopic(new CreateTopicRequest(snsProperties.getName()));
			} catch (Exception e) {
				LOGGER.warn("Attempt to access AWS Local Stack client.createTopic(" + snsProperties.getName()
						+ RETRY_MESSAGE + (i + 1)
						+ WAIT_FOR_LOCALSTACK_MESSAGE);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Restore interrupted state...
				Thread.currentThread().interrupt();
			}
		}

		return null;
	}

	private void initializeDlqQueues(AmazonSQS client) {
		Boolean dlqEnabled = sqsProperties.getDlqenabled();

		// create Dead Letter Queue and set up redrive policy
		if (dlqEnabled) {
			Map<String, String> dlqAttributeMap = new HashMap<>();
			dlqAttributeMap.put("FifoQueue", sqsProperties.getQueuetype().toString());
			dlqAttributeMap.put("DelaySeconds", sqsProperties.getDlqdelay().toString());
			dlqAttributeMap.put("MaximumMessageSize", sqsProperties.getDlqmaxmessagesize());
			dlqAttributeMap.put("MessageRetentionPeriod", sqsProperties.getDlqmessageretentionperiod());
			dlqAttributeMap.put("ReceiveMessageWaitTimeSeconds", sqsProperties.getDlqwaittime().toString());
			dlqAttributeMap.put("VisibilityTimeout", sqsProperties.getDlqvisibilitytimeout().toString());
			dlqAttributeMap.put("ContentBasedDeduplication", sqsProperties.getDlqcontentbaseddeduplication().toString());

			// retry the operation until the localstack responds
			for (int i = 0; i < maxRetries; i++) {
				try {
					dlqUrl = client.createQueue(new CreateQueueRequest(sqsProperties.getDLQQueueName()).withAttributes(dlqAttributeMap)).getQueueUrl();

					if (profileCheck(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS) || profileCheck(BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT))  {
						dlqUrl = dlqUrl.replace(LOCALHOST, LOCALSTACK);
						break;
					}
				} catch (Exception e) {
					LOGGER.warn("Attempt to access AWS Local Stack client.createQueue(" + sqsProperties.getDLQQueueName()
							+ RETRY_MESSAGE + (i + 1)
							+ WAIT_FOR_LOCALSTACK_MESSAGE);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Restore interrupted state...
					Thread.currentThread().interrupt();
				}

			}
		}
	}

	private void createQueues(AmazonSQS client){
		Boolean dlqEnabled = sqsProperties.getDlqenabled();

		GetQueueAttributesResult dlqAttributesResult = null;
		String redrivePolicy = null;

		if (dlqEnabled) {
			dlqAttributesResult = getDlqAttributes(client);
		}

		if (dlqEnabled && dlqAttributesResult != null) {
			redrivePolicy = "{\"maxReceiveCount\":\"" + sqsProperties.getMaxreceivecount() + "\", \"deadLetterTargetArn\":\""
					+ dlqAttributesResult.getAttributes().get(QueueAttributeName.QueueArn.name()) + "\"}";
		}

		Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put("FifoQueue", sqsProperties.getQueuetype().toString());
		attributeMap.put("DelaySeconds", sqsProperties.getDelay().toString());
		attributeMap.put("MaximumMessageSize", sqsProperties.getMaxmessagesize());
		attributeMap.put("MessageRetentionPeriod", sqsProperties.getMessageretentionperiod());
		attributeMap.put("ReceiveMessageWaitTimeSeconds", sqsProperties.getWaittime().toString());
		attributeMap.put("VisibilityTimeout", sqsProperties.getVisibilitytimeout().toString());
		attributeMap.put("ContentBasedDeduplication", sqsProperties.getContentbaseddeduplication().toString());

		if (dlqEnabled && redrivePolicy != null) {
			attributeMap.put(QueueAttributeName.RedrivePolicy.name(), redrivePolicy);
		}

		// retry the operation until the localstack responds
		for (int i = 0; i < maxRetries; i++) {
			try {
				client.createQueue(new CreateQueueRequest(sqsProperties.getQueueName()).withAttributes(attributeMap));
				break;
			} catch (Exception e) {
				LOGGER.warn("Attempt to access AWS Local Stack client.createQueue(" + sqsProperties.getQueueName()
					+ RETRY_MESSAGE + (i + 1)
					+ WAIT_FOR_LOCALSTACK_MESSAGE);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Restore interrupted state...
				Thread.currentThread().interrupt();
			}
		}
	}

	private GetQueueAttributesResult getDlqAttributes(AmazonSQS client) {
		GetQueueAttributesResult getQueueAttributesResult = null;

		GetQueueAttributesRequest getAttributesRequest =
				new GetQueueAttributesRequest(dlqUrl).withAttributeNames(QueueAttributeName.QueueArn);

		// retry the operation until the localstack responds
		for (int i = 0; i < maxRetries; i++) {
			try {
				getQueueAttributesResult = client.getQueueAttributes(getAttributesRequest);
				break;
			} catch (Exception e) {
				LOGGER.warn("Attempt to access DLQ Attributes through AWS Local Stack client.getQueueAttributes(..) failed on try # " + (i + 1)
						+ WAIT_FOR_LOCALSTACK_MESSAGE);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Restore interrupted state...
				Thread.currentThread().interrupt();
			}

		}

		return getQueueAttributesResult;
	}

	private void subscribeTopicToQueue(AmazonSNS client, CreateTopicResult result) {
		// retry the operation until the localstack responds
		for (int i = 0; i < maxRetries; i++) {
			try {
				client.subscribe(result.getTopicArn(), "sqs", sqsProperties.getEndpoint());
				break;
			} catch (Exception e) {
				LOGGER.warn("Attempt to access AWS Local Stack SnsServiceclient.subscribe(" + result.getTopicArn()
						+ RETRY_MESSAGE + (i + 1)
						+ WAIT_FOR_LOCALSTACK_MESSAGE);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Restore interrupted state...
				Thread.currentThread().interrupt();
			}
		}
	}

	private AmazonSQS getLocalIntSQS() {
		if (sqsProperties.getSqsBaseUrl().contains(LOCALHOST)) {
			sqsProperties.setEndpoint(sqsProperties.getEndpoint().replace(LOCALHOST, LOCALSTACK));
		}

		return AmazonSQSClientBuilder.standard().
				withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsProperties.getSqsBaseUrl(), snsProperties.getRegion())).
				withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(snsProperties.getAccessKey(), snsProperties.getSecretKey()))).build();
	}

	private AmazonSNS getLocalIntSNS() {
		if (snsProperties.getSnsBaseUrl().contains(LOCALHOST)) {
			snsProperties.setEndpoint(snsProperties.getEndpoint().replace(LOCALHOST, LOCALSTACK));
		}

		return AmazonSNSClientBuilder.standard().
				withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(snsProperties.getSnsBaseUrl(), snsProperties.getRegion())).
				withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(snsProperties.getAccessKey(), snsProperties.getSecretKey()))).build();
	}

	private void createLocalstackServices(){
		//Creates a SQS queue
		if (sqsProperties.getEnabled()) {
			AmazonSQS client;
			if (profileCheck(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				client = TestUtils.getClientSQS();
			} else {
				client = getLocalIntSQS();
			}

			initializeDlqQueues(client);
			createQueues(client);
		}

		if (snsProperties.getEnabled()) {
			AmazonSNS client;
			if (profileCheck(BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				client = TestUtils.getClientSNS();
			} else {
				client = getLocalIntSNS();
			}

			//Creates a SNS topic
			CreateTopicResult result = createTopics(client);

			if (sqsProperties.getEnabled()) {
				//Subscribes the topic to the queue
				if (result == null) {
					throw new NullPointerException("result is null");
				} else subscribeTopicToQueue(client, result);
			}
		}
	}
}
