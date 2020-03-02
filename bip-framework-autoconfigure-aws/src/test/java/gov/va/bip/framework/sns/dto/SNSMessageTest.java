package gov.va.bip.framework.sns.dto;

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
