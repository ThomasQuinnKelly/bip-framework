package gov.va.bip.framework.sqs.services;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import gov.va.bip.framework.sqs.dto.SendMessageResponse;

public interface SqsService {

    /**
     * Send a SQS Message
     *
     * @param message
     * @return returns a JMS ID
     */
    public SendMessageResponse sendMessage(SQSTextMessage message);

    /**
     * Create a TextMessage
     *
     * @param message
     * @return returns a TextMessage
     */
    public SQSTextMessage createTextMessage(String message);
    
}