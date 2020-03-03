package gov.va.bip.framework.sns.dto;

import org.junit.Assert;
import org.junit.Test;

/*
 * This class exists to parse out a SNS message sent to SQS
 */
public class SNSMessageTest {
        String messageId = "5555";
        String type= "String";
        String timestamp="20:00pm";
        String message="test message";
        String topicArn="50000000:ARN";


    @Test
    public void testSNSMessage() {
        SNSMessage snsMessage = new SNSMessage(messageId, type, timestamp, message, topicArn);

        Assert.assertEquals(message, snsMessage.getMessage());
        Assert.assertEquals(messageId, snsMessage.getMessageId());
        Assert.assertEquals(type, snsMessage.getType());
        Assert.assertEquals(timestamp, snsMessage.getTimestamp());
        Assert.assertEquals(topicArn, snsMessage.getTopicArn());
    }
}
