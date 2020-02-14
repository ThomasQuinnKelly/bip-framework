package gov.va.bip.framework.sns.services;

import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public interface SnsService{

    PublishResult publish(PublishRequest var1);
}