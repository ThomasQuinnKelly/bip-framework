package gov.va.bip.framework.sns.services.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import gov.va.bip.framework.exception.SnsException;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import gov.va.bip.framework.sqs.config.SqsProperties;
import gov.va.bip.framework.validation.Defense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SnsServiceImpl implements SnsService {

	private final Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);
	public static final String ERROR_MESSAGE = "Error Message: {}";
	
	@Autowired
	SnsProperties snsProperties;

	@Autowired
	SqsProperties sqsProperties;

	@Autowired
	AmazonSNS amazonSNS;

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
		}

	return amazonSNS.publish(var1);
	}

	@Override
	public SubscribeResult subscribe(SubscribeRequest var1) {

		try {
			Defense.notNull(var1.getTopicArn(), "Topic-arn can't be null");
			logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", var1.getTopicArn());
			Defense.notNull(var1.getProtocol(), "Protocol can't be null");
			logger.info("Sent request to retrieve protocol, protocol: '{}'", var1.getProtocol());
			Defense.notNull(var1.getEndpoint(), "Endpoint can't be null");
			logger.info("Sent request to retrieve protocol, protocol: '{}'", var1.getEndpoint());

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SnsException(MessageKeys.BIP_SNS_TOPICARN_RETRIEVE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}
		return amazonSNS.subscribe(var1);
	}
}





