package gov.va.bip.framework.localstack.autoconfigure;

import cloud.localstack.Localstack;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.annotation.LocalstackDockerConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sqs.config.SqsProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.GetTopicAttributesResponse;

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
@EnableConfigurationProperties({ LocalstackProperties.class, SqsProperties.class, SnsProperties.class })
@ConditionalOnProperty(name = "bip.framework.localstack.enabled", havingValue = "true")
@Primary
@Order( Ordered.HIGHEST_PRECEDENCE )
public class LocalstackAutoConfiguration {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(LocalstackAutoConfiguration.class);

	@Autowired
	private LocalstackProperties localstackProperties;

	@Autowired
	private SqsProperties sqsProperties;

	@Autowired
	private SnsProperties snsProperties;

	private static Integer MAX_RETRIES = 60;

	@Value("${bip.framework.localstack.externalHostName:localhost}")
	private String externalHostName;

	@Value("${bip.framework.localstack.imageTag:latest}")
	private String imageTag;

	@Value("${bip.framework.localstack.pullNewImage:true}")
	private boolean pullNewImage;

	@Value("${bip.framework.localstack.randomizePorts:false}")
	private boolean randomizePorts;

	private Map<String, String> environmentVariables = new HashMap<>();

	/**
	 * Start embedded AWS servers on context load
	 *
	 * @throws IOException
	 */
	@PostConstruct
	public void startAwsLocalStack() {
		if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
			// AWS localstack already running, not trying to re-start
			return;
		} else if (Localstack.INSTANCE != null) {
			// Clean the localstack
			cleanAwsLocalStack();

			Localstack.INSTANCE.startup(buildLocalstackDockerConfiguration());

			//configureAwsLocalStack();

			if (sqsProperties.getEnabled()) {
				createQueues();
			}

			CreateTopicResult result = null;
			if (snsProperties.getEnabled()) {
				result = createTopics();
			}

			if (snsProperties.getEnabled() & sqsProperties.getEnabled()) {
				SubscribeTopicToQueue(result);
			}
		}
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

	//public abstract void configureAwsLocalStack();

	/**
	 * Stop embedded AWS servers on context destroy
	 */
	@PreDestroy
	public void stopAwsLocalStack() {
		// Stop the localstack
		if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
			Localstack.INSTANCE.stop();
		}

		// Clean the localstack
		cleanAwsLocalStack();
	}

	/**
	 * Clean AWS Localstack containers
	 */
	private void cleanAwsLocalStack() {
		// Get containers Ids
		DockerExe newDockerExe = new DockerExe();
		String listContainerIds =
				newDockerExe.execute(Arrays.asList("ps", "--no-trunc", "-aq", "--filter", "ancestor=localstack/localstack"));

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

	private CreateTopicResult createTopics() {

		AmazonSNS client = TestUtils.getClientSNS();

		snsProperties.getAllTopicProperties();

		// retry the operation until the localstack responds
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				return client.createTopic(new CreateTopicRequest(snsProperties.getName()));
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
//					throw new BipRuntimeException("AWS Local Stack (SQS create " + sqsProperties.getQueueName()
//							+ ") failed to initialize after " + MAX_RETRIES + " tries.");
				}
				LOGGER.warn("Attempt to access AWS Local Stack client.createTopic(" + snsProperties.getName()
						+ ") failed on try # " + (i + 1)
						+ ", waiting for AWS localstack to finish initializing.");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// NOSONAR do nothing
			}
		}

		return null;
	}

	//TODO: Incorporate BipRuntimeException here
	private void createQueues() {
		AmazonSQS client = TestUtils.getClientSQS();


		//TODO: build in support for mutiple queues in some way.
		sqsProperties.getAllQueueProperties();

		String deadletterQueueUrl = null;
		GetQueueAttributesResult queueAttributesResult = null;

		// retry the operation until the localstack responds
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				deadletterQueueUrl = client.createQueue(sqsProperties.getDLQQueueName()).getQueueUrl();
				break;
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
//					throw new BipRuntimeException("AWS Local Stack (SQS create " + sqsProperties.getDLQQueueName()
//							+ ") failed to initialize after " + MAX_RETRIES + " tries.");
				}
				LOGGER.warn("Attempt to access AWS Local Stack client.createQueue(" + sqsProperties.getDLQQueueName()
						+ ") failed on try # " + (i + 1)
						+ ", waiting for AWS localstack to finish initializing.");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// NOSONAR do nothing
			}
		}

		GetQueueAttributesRequest getAttributesRequest =
				new GetQueueAttributesRequest(deadletterQueueUrl).withAttributeNames(QueueAttributeName.QueueArn);

		// retry the operation until the localstack responds
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				queueAttributesResult = client.getQueueAttributes(getAttributesRequest);
				break;
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
//					throw new BipRuntimeException(
//							"AWS Local Stack (SQS Get DLQ Attributes) failed to initialize after " + MAX_RETRIES + " tries.");
				}
				LOGGER.warn("Attempt to access AWS Local Stack client.getQueueAttributes(..) failed on try # " + (i + 1)
						+ ", waiting for AWS localstack to finish initializing.");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// NOSONAR do nothing
			}
		}

		String redrivePolicy = "{\"maxReceiveCount\":\"1\", \"deadLetterTargetArn\":\""
				+ queueAttributesResult.getAttributes().get(QueueAttributeName.QueueArn.name()) + "\"}";

		Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put("DelaySeconds", sqsProperties.getDelay().toString());
		attributeMap.put("MaximumMessageSize", sqsProperties.getMaxmessagesize());
		attributeMap.put("MessageRetentionPeriod", sqsProperties.getMessageretentionperiod());
		attributeMap.put("ReceiveMessageWaitTimeSeconds", sqsProperties.getWaittime().toString());
		attributeMap.put("VisibilityTimeout", sqsProperties.getVisibilitytimeout().toString());
		attributeMap.put("FifoQueue", sqsProperties.getQueuetype().toString());
		attributeMap.put("ContentBasedDeduplication", sqsProperties.getContentbasedduplication().toString());
		attributeMap.put(QueueAttributeName.RedrivePolicy.name(), redrivePolicy);

		// retry the operation until the localstack responds
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				client.createQueue(new CreateQueueRequest(sqsProperties.getQueueName()).withAttributes(attributeMap));
				break;
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
//					throw new BipRuntimeException("AWS Local Stack (SQS create " + sqsProperties.getQueueName()
//							+ ") failed to initialize after " + MAX_RETRIES + " tries.");
				}
				LOGGER.warn("Attempt to access AWS Local Stack client.createQueue(" + sqsProperties.getQueueName()
						+ ") failed on try # " + (i + 1)
						+ ", waiting for AWS localstack to finish initializing.");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// NOSONAR do nothing
			}
		}
	}

	private void SubscribeTopicToQueue(CreateTopicResult result) {

		AmazonSNS SnsServiceclient = TestUtils.getClientSNS();
		AmazonSQS SqsServciceclient = TestUtils.getClientSQS();

		snsProperties.getAllTopicProperties();
		sqsProperties.getAllQueueProperties();

		// retry the operation until the localstack responds
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				SnsServiceclient.subscribe(result.getTopicArn(), "sqs", sqsProperties.getEndpoint());
				break;
			} catch (Exception e) {
				if (i == MAX_RETRIES - 1) {
//					throw new BipRuntimeException("AWS Local Stack (SQS create " + sqsProperties.getQueueName()
//							+ ") failed to initialize after " + MAX_RETRIES + " tries.");
				}
				LOGGER.warn("Attempt to access AWS Local Stack SnsServiceclient.subscribe(" + snsProperties.getTopicArn()
						+ ") failed on try # " + (i + 1)
						+ ", waiting for AWS localstack to finish initializing.");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// NOSONAR do nothing
			}
		}
	}
}
