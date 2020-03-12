package gov.va.bip.framework.sns.dto;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/*
 * This class exists to parse out a SNS message sent to SQS
 */
public class SNSMessageTest {

    private SNSMessage snsMessage;


    @Test
    public void constructorTest(){
        snsMessage = new SNSMessage("5555", "String","20:00pm", "Test Message", "50000:ARN");
        assertThat(snsMessage.getMessageId() != null);
        assertThat(snsMessage.getType() != null);
        assertThat(snsMessage.getTimestamp() != null);
        assertThat(snsMessage.getMessage() != null);
        assertThat(snsMessage.getTopicArn() != null);
    }

    /**
     * Test get messageID from SNSMessage class
     */
    @Test
    public void testgetMessageId() {
        String messageId = "5555";
        SNSMessage instance = new SNSMessage();
        instance.getMessageId();
        Assert.assertNotEquals(messageId, instance.getMessageId());
    }

    /**
     * Test get type from SNSMessage class
     */
    @Test
    public void testgetType() {
        String type= "String";
        SNSMessage instance = new SNSMessage();
        instance.getType();
        Assert.assertNotEquals(type, instance.getType());
    }

    /**
     * Test get timestamp from SNSMessage class
     */
    @Test
    public void testgetTimeStamp() {
        String timestamp="20:00pm";
        SNSMessage instance = new SNSMessage();
        instance.getTimestamp();
        Assert.assertNotEquals(timestamp, instance.getTimestamp());
    }

    /**
     * Test get message from SNSMessage class
     */
    @Test
    public void testgetMessage() {
        String message="test message";
        SNSMessage instance = new SNSMessage();
        instance.getMessage();
        Assert.assertNotEquals(message, instance.message);
    }

    /**
     * Test get TopicArn from SNSMessage class
     */
    @Test
    public void testgetTopicArn() {
        String topicArn="50000000:ARN";
        SNSMessage instance = new SNSMessage();
        instance.getTopicArn();
        Assert.assertNotEquals(topicArn, instance.getTopicArn());
    }
    /**
     * Test set messageID from SNSMessage class
     */
    @Test
    public void testsetMessageId() {
        String messageId = "5555";
        SNSMessage instance = new SNSMessage();
        instance.setMessageId(messageId);
        Assert.assertEquals(messageId, instance.getMessageId());
    }

    /**
     * Test set type from SNSMessage class
     */
    @Test
    public void testsetType() {
        String type= "String";
        SNSMessage instance = new SNSMessage();
        instance.setType(type);
        Assert.assertEquals(type, instance.getType());
    }

    /**
     * Test set timestamp from SNSMessage class
     */
    @Test
    public void testsetTimeStamp() {
        String timestamp="20:00pm";
        SNSMessage instance = new SNSMessage();
        instance.setTimestamp(timestamp);
        Assert.assertEquals(timestamp, instance.getTimestamp());
    }

    /**
     * Test set message from SNSMessage class
     */
    @Test
    public void testsetMessage() {
        String message="test message";
        SNSMessage instance = new SNSMessage();
        instance.setMessage(message);
        Assert.assertEquals(message, instance.getMessage());
    }

    /**
     * Test set TopicArn from SNSMessage class
     */
    @Test
    public void testsetTopicArn() {
        String topicArn="50000000:ARN";
        SNSMessage instance = new SNSMessage();
        instance.setTopicArn(topicArn);
        Assert.assertEquals(topicArn, instance.getTopicArn());
    }
}
