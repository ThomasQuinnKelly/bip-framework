package gov.va.bip.framework.sns.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import gov.va.bip.framework.exception.SnsException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import gov.va.bip.framework.sqs.config.SqsProperties;
import gov.va.bip.framework.validation.Defense;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.NEWLINE;

@Service
public class SnsServiceImpl implements SnsService {

	private final BipLogger logger = BipLoggerFactory.getLogger(SnsServiceImpl.class);
	public static final String ERROR_MESSAGE = "Error Message: {}";


	@Autowired
	SnsProperties snsProperties;

	@Autowired
	SqsProperties sqsProperties;

	@Autowired
	AmazonSNS amazonSNS;

	@Override
	public CreateTopicResult createTopic(CreateTopicRequest var1) {

		try {
			Defense.notNull(snsProperties.getTopic(), "SNS topic can't be null");
			logger.info("Sent request to create SNS topic, topic: '{}'", snsProperties.getTopic());

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SnsException(MessageKeys.BIP_SNS_TOPIC_CREATE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else throw new SnsException(MessageKeys.BIP_SNS_TOPIC_CREATE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		var1 = new CreateTopicRequest(snsProperties.getTopic());

		return amazonSNS.createTopic(var1);
	}

	@Override
	public SubscribeResult subscribe(String var1, String var2, String var3) {
		CreateTopicResult result = null;

		try {
			Defense.notNull(result.getTopicArn(), "Topic-arn can't be null");
			logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", result.getTopicArn());

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SnsException(MessageKeys.BIP_SNS_TOPICARN_RETRIEVE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else throw new SnsException(MessageKeys.BIP_SNS_TOPICARN_RETRIEVE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		try {
			Defense.notNull(sqsProperties.getEndpoint(), "Sqs endpoint can't be null");
			logger.info("Sent request to retrieve Sqs endpoint, Sqs endpoint: '{}'", sqsProperties.getEndpoint());

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SnsException(MessageKeys.BIP_SQS_ENDPOINT_RETRIEVE_EXCEPTION, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else throw new SnsException(MessageKeys.BIP_SQS_ENDPOINT_RETRIEVE_EXCEPTION, MessageSeverity.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		var1 = result.getTopicArn();
		var2 = "sqs";
		var3 = sqsProperties.getEndpoint();

	return amazonSNS.subscribe(var1, var2, var3);
	}

	@Override
	public PublishResult publish(PublishRequest var1) {

		try {
			Defense.notNull(var1.getTopicArn(), "Topic-arn can't be null");
			logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", var1.getTopicArn());

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SnsException(MessageKeys.BIP_SNS_TOPICARN_RETRIEVE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else throw new SnsException(MessageKeys.BIP_SNS_TOPICARN_RETRIEVE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

	return amazonSNS.publish(var1);
	}
}





