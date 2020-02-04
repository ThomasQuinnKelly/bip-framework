package gov.va.bip.framework.sqs.services;

import com.amazon.sqs.javamessaging.message.SQSMessage;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import gov.va.bip.framework.sqs.dto.BipMessage;
import gov.va.bip.framework.sqs.dto.SendMessageResponse;

import javax.jms.Message;
import javax.jms.TextMessage;

public interface SqsService {

//    /**
//     * Send a Message
//     *
//     * @param message
//     * @return returns a JMS ID
//     */
//    public SendMessageResponse sendMessage(javax.jms.TextMessage message);

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