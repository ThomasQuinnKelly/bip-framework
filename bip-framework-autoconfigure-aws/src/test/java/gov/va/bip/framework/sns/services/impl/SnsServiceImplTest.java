package gov.va.bip.framework.sns.services.impl;


import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import gov.va.bip.framework.exception.SnsException;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.services.SnsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class SnsServiceImplTest {

	@Autowired
	@InjectMocks
	private SnsService snsService = new SnsServiceImpl();

	@InjectMocks
	SnsProperties snsProperties = new SnsProperties();


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
		String topicArnValue = "arn:aws:sns:us-east-1:000000000000:test_my_topic";

		PublishRequest request = new PublishRequest();

		request.setTopicArn(topicArnValue);
		logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", request.getTopicArn());

		Assert.assertNotNull(logger);
		Assert.assertNotNull(request.getTopicArn());


		try {
			snsService.publish(request);
		} catch(Exception e){
			Assert.assertNotNull(request.getTopicArn());
		}
	}

	@Test
	public void testSubscribeValuesisNotNull(){
		Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);

		String topicArnValue = "arn:aws:sns:us-east-1:000000000000:test_my_topic";
		String protocolValue = "sqs";
		String endpointValue = "http://localhost:4576/queue/sub_new_queue";

		SubscribeRequest request = new SubscribeRequest();

		request.setTopicArn(topicArnValue);
		request.setProtocol(protocolValue);
		request.setEndpoint(endpointValue);
		logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", request.getTopicArn());
		logger.info("Sent request to retrieve protocol, protocol: '{}'", request.getProtocol());
		logger.info("Sent request to retrieve endpoint, endpoint: '{}'", request.getEndpoint());

		Assert.assertNotNull(logger);
		Assert.assertNotNull(request.getTopicArn());
		Assert.assertNotNull(request.getProtocol());
		Assert.assertNotNull(request.getEndpoint());

		try {
			snsService.subscribe(request);
		} catch(Exception e){
			Assert.assertNotNull(request.getTopicArn());
		}
	}

	@Test
	public void testSubscribeError(){
		Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);

		String topicArnValue = null;
		String protocolValue = "sqs";
		String endpointValue = "http://localhost:4576/queue/sub_new_queue";

		SubscribeRequest request = new SubscribeRequest();

		request.setTopicArn(topicArnValue);
		request.setProtocol(protocolValue);
		request.setEndpoint(endpointValue);
		logger.info("Sent request to retrieve topic-arn, topic-arn: '{}'", request.getTopicArn());
		logger.info("Sent request to retrieve protocol, protocol: '{}'", request.getProtocol());
		logger.info("Sent request to retrieve endpoint, endpoint: '{}'", request.getEndpoint());

		Assert.assertNotNull(logger);

		Assert.assertNotNull(request.getProtocol());
		Assert.assertNotNull(request.getEndpoint());

		try {
			snsService.subscribe(request);
		} catch(Exception e){
			Assert.assertNull(request.getTopicArn());
			}
	}
}
