package gov.va.bip.framework.sqs.services.impl;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import gov.va.bip.framework.exception.SqsException;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.sqs.dto.SendMessageResponse;
import gov.va.bip.framework.sqs.services.SqsService;
import gov.va.bip.framework.validation.Defense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Enumeration;

@Service
public class SqsServiceImpl implements SqsService {

	private static final String ERROR_MESSAGE = "Error Message: {}";

	private Logger logger = LoggerFactory.getLogger(SqsServiceImpl.class);

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
			messageId = jmsOperations.execute((session, producer) -> {
				message.setJMSTimestamp(System.currentTimeMillis());
				producer.send(message);
				logger.info("Sent JMS message with payload='{}', id: '{}'", message, message.getJMSMessageID());
				return message.getJMSMessageID();
			});
		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
			else
				throw new SqsException(MessageKeys.BIP_SQS_MESSAGE_TRANSFER_ERROR, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());

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

	/**
	 * Creates a TextMessage
	 */
	@Override
	public SQSTextMessage createTextMessage(String message) {

		try {
			Defense.notNull(message, "Message can't be null");
			SQSTextMessage result = (SQSTextMessage) connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
					.createTextMessage(message);

			Enumeration<String> s = result.getPropertyNames();

			while (s.hasMoreElements()) {
				String t = s.nextElement();
				logger.info(t);
			}
			return result;

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