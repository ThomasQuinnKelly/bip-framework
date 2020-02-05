package gov.va.bip.framework.sns.services.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import gov.va.bip.framework.sqs.config.SqsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsServiceImpl implements SnsService {

	@Autowired
	SnsProperties snsProperties;

	@Autowired
	SqsProperties sqsProperties;

	@Autowired
	AmazonSNS amazonSNS;

	/*
	@Override
	public CreateTopicResult createTopic(CreateTopicRequest var1){
		var1 = new CreateTopicRequest(snsProperties.getTopic());

		return createTopic(var1);
	};

	public DeleteTopicResult deleteTopic(DeleteTopicRequest var1){
		//IMPORTANT: When you delete a topic, you also delete all subscriptions to the topic.
		// Delete an Amazon SNS topic.
		var1 = new DeleteTopicRequest(var1.getTopicArn());
		return deleteTopic(var1);
	}

	@Override
	public SubscribeResult subscribe(String var1, String var2, String var3) {
		CreateTopicResult result = null;
		var1 = result.getTopicArn();

		var2 = "sqs";

		var3 = sqsProperties.getEndpoint();

		return subscribe(var1, var2, var3);

	}

	 */

	public PublishResult publish(PublishRequest var1){
		// Publish a message to an Amazon SNS topic.
		return amazonSNS.publish(var1);
		//var1 = new PublishRequest(var1.getTopicArn(), var1.getMessage());
		//return publish(var1);
	}

}





