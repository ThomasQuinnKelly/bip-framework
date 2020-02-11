package gov.va.bip.framework.sns.services.impl;

import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import gov.va.bip.framework.sqs.config.SqsProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(MockitoJUnitRunner.class)
public class SnsServiceImplTest {

	@Autowired
	@InjectMocks
	private SnsService snsService = new SnsServiceImpl();

	@Test
	public void testCreateTopicResult(){
		CreateTopicRequest createTopic = (new CreateTopicRequest("Test My Topic"));
		Assert.assertNotNull(createTopic);

	}

	@Test
	public void testSubscribeResult(){
		SubscribeRequest request = new SubscribeRequest("arn:aws:sns:us-east-1:000000000000:test_my_topic", "sqs", "http://localhost:4576/queue/sub_new_queue");
		Assert.assertNotNull(request);
	}
}
