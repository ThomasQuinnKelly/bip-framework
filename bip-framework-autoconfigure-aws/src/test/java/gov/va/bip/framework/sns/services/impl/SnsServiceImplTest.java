package gov.va.bip.framework.sns.services.impl;


import com.amazonaws.services.sns.model.PublishRequest;
import gov.va.bip.framework.exception.SnsException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SnsServiceImplTest {

	@Test
	public void testTopicArnisNull(){

		SnsServiceImpl snsService = new SnsServiceImpl();

		assertThrows(SnsException.class, new Executable() {

			@Override
			public void execute() throws Throwable {

				PublishRequest request = new PublishRequest();

				request.setTopicArn(null);
				snsService.publish(request);
			}
		});
	}

	@Test
	public void testTopicArnisNotNull(){
		Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);
		SnsServiceImpl snsService = new SnsServiceImpl();
		String topicArnValue = "arn:aws:sns:us-east-1:000000000000:test_my_topic";

		PublishRequest request = new PublishRequest();

		request.setTopicArn(topicArnValue);
		logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", request.getTopicArn());

		Assert.assertNotNull(logger);
		Assert.assertNotNull(request.getTopicArn());
	}
}
