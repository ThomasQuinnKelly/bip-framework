package gov.va.bip.framework.sqs.services.impl;

import com.amazon.sqs.javamessaging.message.SQSMessage;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.sqs.dto.SendMessageResponse;
import gov.va.bip.framework.sqs.exception.SqsException;
import gov.va.bip.framework.sqs.services.SqsService;
import gov.va.bip.framework.validation.Defense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Enumeration;

@Service
public class SqsServiceImpl implements SqsService {

	private static final String ERROR_MESSAGE = "Error Message: {}";

	private BipLogger logger = BipLoggerFactory.getLogger(SqsServiceImpl.class);

	@Resource
	JmsOperations jmsOperations;

	@Autowired
	ConnectionFactory connectionFactory;

	/**
	 * Sends the message to the main queue.
	 */
	@Override
	@ManagedOperation
	public SendMessageResponse sendMessage(SQSTextMessage message) {

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
//	public SendMessageResponse sendMessage() {
//		TextMessage tm = new TextMessage() {
//
//		}
//
//		return null;
//	}

	/**
	 * Creates a TextMessage
	 */
	@Override
	public SQSTextMessage createTextMessage(String message) {

		try {

			Defense.notNull(message, "Message can't be null");
			return (SQSTextMessage) connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
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
}

//public class SqsServiceImpl implements SqsService {
//
//	private static final String ERROR_MESSAGE = "Error Message: {}";
//
//	private BipLogger logger = BipLoggerFactory.getLogger(SqsServiceImpl.class);
//
//	@Resource
//	JmsOperations jmsOperations;
//
//	@Autowired
//	SQSConnectionFactory connectionFactory;
//
//	@Autowired
//	DestinationResolver destinationResolver;
//
//	@Override
//	public TextMessage createTextMessage(String message) {
//
//		try {
//
//			Defense.notNull(message, "Message can't be null");
//			return connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
//					.createTextMessage(message);
//
//		} catch (JMSException e) {
//			logger.error(ERROR_MESSAGE, e);
//			if (e.getMessage() != null)
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_JMS_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
//			else
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_JMS_FAILED, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e);
//
//		} catch (Exception e) {
//			logger.error(ERROR_MESSAGE, e);
//			if (e.getMessage() != null)
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
//			else
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_CREATE_EXCEPTION, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e);
//
//		}
//
//	}
//
//	/**
//	 * Sends the message to the main queue.
//	 */
//	@Override
//	@ManagedOperation
//	public SendMessageResponse sendMessage(Message message) {
//
//		String messageId = null;
//		SendMessageResponse sendMessageResponse = new SendMessageResponse();
//		try {
//			Defense.notNull(message, "Message can't be null");
//			messageId = jmsOperations.execute(new ProducerCallback<String>() {
//				@Override
//				public String doInJms(Session session, MessageProducer producer) throws JMSException {
//					message.setJMSTimestamp(System.currentTimeMillis());
//					producer.send(message);
//					logger.info("Sent JMS message with payload='{}', id: '{}'", message, message.getJMSMessageID());
//					return message.getJMSMessageID();
//				}
//			});
//
//		} catch (Exception e) {
//			logger.error(ERROR_MESSAGE, e);
//			if (e.getMessage() != null)
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR_MESSAGE, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
//			else
//				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR, MessageSeverity.ERROR,
//						HttpStatus.INTERNAL_SERVER_ERROR, e);
//
//		}
//
//		if (messageId == null) {
//			logger.error("Error Message: Message ID cannot be null after message has been sent");
//			throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_FAILED_MESSAGE_ID_NULL, MessageSeverity.ERROR,
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		sendMessageResponse.setMessageId(messageId);
//		sendMessageResponse.setStatusCode(HttpStatus.OK.toString());
//		return sendMessageResponse;
//	}
//
////	@Override
////	public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest) {
////
////		CreateQueueResult createQueueResult = null;
////
////		try {
////
////			SQSConnection connection = connectionFactory.createConnection();
////
////			// Get the wrapped client
////			AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
////
////			// Create an SQS queue named MyQueue, if it doesn't already exist
////			if (!client.queueExists("MyQueue")) {
////				createQueueResult = client.createQueue("MyQueue");
////			}
////
////		} catch (JMSException e) {
////			e.printStackTrace();
////		}
////
////
////
////		return createQueueResult;
////	}
////
////	@Override
////	public CreateQueueResult createQueue(String queueName) {
////		return createQueue(new CreateQueueRequest(queueName));
////	}
////
////	@Override
////	public ListQueuesResult listQueues() {
////
////		SQSConnection connection = null;
////		try {
////			connection = connectionFactory.createConnection();
////		} catch (JMSException e) {
////			e.printStackTrace();
////		}
////
////		return connection.getWrappedAmazonSQSClient().getAmazonSQSClient().listQueues();
////	}
//////
//////	@Override
//////	public Message receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
//////
//////		Message receivedMessage = null
//////
//////		MessageConsumer consumer = null;
//////
//////		Defense.notNull(receiveMessageRequest, "Receive Message Request can't be null");
//////		Defense.notNull(receiveMessageRequest.getQueueUrl(), "Queue Url can't be null");
//////
//////		try {
//////			Connection c = connectionFactory.createConnection();
//////			Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
//////
//////			Destination queue = destinationResolver.resolveDestinationName(s,"",false);
//////
//////			consumer = s.createConsumer(queue);
//////
//////			c.start();
//////
//////			receivedMessage = consumer.receive(1000);
//////
//////			if (receivedMessage != null) {
//////				System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
//////				System.out.println("Received ID: " + receivedMessage.getJMSMessageID());
//////
//////			} else {
//////				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_FAILED_MESSAGE_ID_NULL, MessageSeverity.ERROR,
//////						HttpStatus.INTERNAL_SERVER_ERROR);
//////			}
//////
//////			c.stop();
//////
//////		} catch (JMSException e) {
//////			e.printStackTrace();
//////		}
//////
//////		return receivedMessage;
//////
//////		// Start receiving incoming messages
////////		connection.start();
////////
////////		Call the receive method on the consumer with a timeout set to 1 second, and then print the contents of the received message.
////////
////////		After receiving a message from a standard queue, you can access the contents of the message.
////////
////////// Receive a message from 'MyQueue' and wait up to 1 second
////////				Message receivedMessage = consumer.receive(1000);
////////
////////// Cast the received message as TextMessage and display the text
////////		if (receivedMessage != null) {
////////			System.out.println("Received: " + ((TextMessage) receivedMessage).getText());
////////		}
////////
//////		return null;
//////	}
////
////
////	@Override
////	public Message receiveMessage(String queueUrl) {
////		Message receivedMessage = null;
////
////		MessageConsumer consumer = null;
////
////		Defense.notNull(queueUrl, "Queue Url can't be null");
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
////	}
////
//
//}
