package gov.va.bip.framework.sns.services;

import com.amazonaws.services.sns.model.*;

public interface SnsService{

    CreateTopicResult createTopic(CreateTopicRequest var1);

    DeleteTopicResult deleteTopic(DeleteTopicRequest var1);

    //ListSubscriptionsResult listSubscriptions();

    //ListTopicsResult listTopics();

    //SubscribeResult subscribe(String var1, String var2, String var3);

    PublishResult publish(PublishRequest var1);

    //UnsubscribeResult unsubscribe(UnsubscribeRequest var1);

    // ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest var1);


}