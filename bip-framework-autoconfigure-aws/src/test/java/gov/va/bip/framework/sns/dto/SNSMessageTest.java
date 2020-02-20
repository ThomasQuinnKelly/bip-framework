package gov.va.bip.framework.sns.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.bip.framework.sns.config.SnsProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/*
 * This class exists to parse out a SNS message sent to SQS
 */
public class SNSMessageTest {
        String messageId = "5555";
        String type= "String";
        String timestamp="20:00pm";
        String message="test message";
        String topicArn="50000000:ARN";


    public void SNSMessage(String messageId, String type, String timestamp, String message, String topicArn) {
        this.messageId = messageId;
        this.type = type;
        this.timestamp = timestamp;
        this.message = message;
        this.topicArn = topicArn;
    }
}
