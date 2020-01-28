package gov.va.bip.framework.sns.services.impl;

import com.amazonaws.services.sns.model.*;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsServiceImpl implements SnsService {

	@Autowired
	SnsProperties snsProperties;

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

	//ListSubscriptionsResult listSubscriptions();

	//ListTopicsResult listTopics();

	//SubscribeResult subscribe(String var1, String var2, String var3);

	public PublishResult publish(PublishRequest var1){
		// Publish a message to an Amazon SNS topic.
		final String msg = "If you receive this message, publishing a message to an Amazon SNS topic works.";
		var1 = new PublishRequest(var1.getTopicArn(), var1.getMessage());
		return publish(var1);
	}

	//UnsubscribeResult unsubscribe(UnsubscribeRequest var1);

	//ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest var1);
}





