package gov.va.bip.framework.sns.services;

import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

public interface SnsService{

    PublishResult publish(PublishRequest var1);

    SubscribeResult subscribe(SubscribeRequest var1);
}