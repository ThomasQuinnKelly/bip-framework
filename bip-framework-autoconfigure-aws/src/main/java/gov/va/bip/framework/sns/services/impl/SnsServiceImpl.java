package gov.va.bip.framework.sns.services.impl;

import javax.annotation.Resource;
import javax.jms.*;
import javax.jms.Message;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.model.*;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.sqs.dto.SendMessageResponse;
import gov.va.bip.framework.sqs.exception.SqsException;
import gov.va.bip.framework.sqs.services.SnsService;
import gov.va.bip.framework.sqs.services.SqsService;
import gov.va.bip.framework.validation.Defense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;

/**
 * 
 * @author srikanthvanapalli
 * 
 * How to set the Queue attributes programatically ?? 
 * 
 * https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html
 * We need to send a request using the queue URL with action=SetQueueAttributes
 * and specifying the attributes and their values that need to be set. 
 * 
 * Currently we do an implementation for that action and as per the link above
 * we need to write an API to modify queue attributes. This is not something
 * we do with every queue message that is sent out.
 * 
 * Example request: 
 * https://sqs.us-east-2.amazonaws.com/123456789012/MyQueue/
		  	?Action=SetQueueAttributes
			&DelaySeconds=45
			&Expires=2020-12-20T22%3A52%3A43PST
			&Version=2012-11-05
			&AUTHPARAMS
 * 
 * 
 *
 */
@Service
public class SnsServiceImpl implements SnsService {

	private static final String ERROR_MESSAGE = "Error Message: {}";

	private BipLogger logger = BipLoggerFactory.getLogger(SqsServiceImpl.class);

	@Resource
	JmsOperations jmsOperations;

	@Autowired
	SQSConnectionFactory connectionFactory;

	@Autowired
	DestinationResolver destinationResolver;

	@Override
	public TextMessage createTextMessage(String message) {

		try {

			Defense.notNull(message, "Message can't be null");
			return connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
					.createTextMessage(message);

		} catch (JMSException e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_JMS_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_JMS_FAILED, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e);

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_EXCEPTION, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e);

		}

	}

	/**
	 * Sends the message to the main queue.
	 */
	@Override
	@ManagedOperation
	public SendMessageResponse sendMessage(Message message) {

		String messageId = null;
		SendMessageResponse sendMessageResponse = new SendMessageResponse();
		try {
			Defense.notNull(message, "Message can't be null");
			messageId = jmsOperations.execute(new ProducerCallback<String>() {
				@Override
				public String doInJms(Session session, MessageProducer producer) throws JMSException {
					message.setJMSTimestamp(System.currentTimeMillis());
					producer.send(message);
					logger.info("Sent JMS message with payload='{}', id: '{}'", message, message.getJMSMessageID());
					return message.getJMSMessageID();
				}
			});

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e);

		}

		if (messageId == null) {
			logger.error("Error Message: Message ID cannot be null after message has been sent");
			throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_FAILED_MESSAGE_ID_NULL, MessageSeverity.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		sendMessageResponse.setMessageId(messageId);
		sendMessageResponse.setStatusCode(HttpStatus.OK.toString());
		return sendMessageResponse;
	}

//	@Override
//	public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest) {
//
//		CreateQueueResult createQueueResult = null;
//
//		try {
//
//			SQSConnection connection = connectionFactory.createConnection();
//
//			// Get the wrapped client
//			AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
//
//			// Create an SQS queue named MyQueue, if it doesn't already exist
//			if (!client.queueExists("MyQueue")) {
//				createQueueResult = client.createQueue("MyQueue");
//			}
//
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//
//
//
//		return createQueueResult;
//	}
//
//	@Override
//	public CreateQueueResult createQueue(String queueName) {
//		return createQueue(new CreateQueueRequest(queueName));
//	}
//
//	@Override
//	public ListQueuesResult listQueues() {
//
//		SQSConnection connection = null;
//		try {
//			connection = connectionFactory.createConnection();
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//
//		return connection.getWrappedAmazonSQSClient().getAmazonSQSClient().listQueues();
//	}
////
////	@Override
////	public Message receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
////
////		Message receivedMessage = null
////
////		MessageConsumer consumer = null;
////
////		Defense.notNull(receiveMessageRequest, "Receive Message Request can't be null");
////		Defense.notNull(receiveMessageRequest.getQueueUrl(), "Queue Url can't be null");
////
////		try {
////			Connection c = connectionFactory.createConnection();
////			Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
////
////			Destination queue = destinationResolver.resolveDestinationName(s,"",false);
////
////			consumer = s.createConsumer(queue);
////
////			c.start();
////
////			receivedMessage = consumer.receive(1000);
////
////			if (receivedMessage != null) {
////				System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
////				System.out.println("Received ID: " + receivedMessage.getJMSMessageID());
////
////			} else {
////				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_FAILED_MESSAGE_ID_NULL, MessageSeverity.ERROR,
////						HttpStatus.INTERNAL_SERVER_ERROR);
////			}
////
////			c.stop();
////
////		} catch (JMSException e) {
////			e.printStackTrace();
////		}
////
////		return receivedMessage;
////
////		// Start receiving incoming messages
//////		connection.start();
//////
//////		Call the receive method on the consumer with a timeout set to 1 second, and then print the contents of the received message.
//////
//////		After receiving a message from a standard queue, you can access the contents of the message.
//////
//////// Receive a message from 'MyQueue' and wait up to 1 second
//////				Message receivedMessage = consumer.receive(1000);
//////
//////// Cast the received message as TextMessage and display the text
//////		if (receivedMessage != null) {
//////			System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
//////		}
//////
////		return null;
////	}
//
//
//	@Override
//	public Message receiveMessage(String queueUrl) {
//		Message receivedMessage = null;
//
//		MessageConsumer consumer = null;
//
//		Defense.notNull(queueUrl, "Queue Url can't be null");
//
//		try {
//			Connection c = connectionFactory.createConnection();
//			Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//			Destination queue = destinationResolver.resolveDestinationName(s,"",false);
//
//			consumer = s.createConsumer(queue);
//
//			c.start();
//
//			receivedMessage = consumer.receive(1000);
//
//			if (receivedMessage != null) {
//				System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
//				System.out.println("Received ID: " + receivedMessage.getJMSMessageID());
//
//			} else {
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_FAILED_MESSAGE_ID_NULL, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//
//			c.stop();
//
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//
//		return receivedMessage;
//	}
//

}