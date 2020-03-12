package gov.va.bip.framework.sns.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class SnsPropertiesTest {

    //Test SNS Topic
    @Test
    public void testTopic() {
        String topic = "test_my_topic";
        SnsProperties instance = new SnsProperties();
        instance.setTopic(topic);

        assertEquals(Optional.of(instance.getTopic()), Optional.ofNullable(topic));
    }

    //Test SNS Logger
    @Test
    public void testLogger() {
        Logger logger = LoggerFactory.getLogger(SnsProperties.class);
        SnsProperties instance = new SnsProperties();
        instance.setLogger(logger);

        assertEquals(Optional.of(instance.getLogger()), Optional.ofNullable(logger));
    }

    //Test SNS Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        SnsProperties instance = new SnsProperties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.getEnabled()), Optional.ofNullable(enabled));
    }

    //Test SNS Property name
    @Test
    public void testName() {
        String name = "test_my_topic";
        SnsProperties instance = new SnsProperties();
        instance.setName(name);

        assertEquals(Optional.of(instance.getName()), Optional.ofNullable(name));
    }

    //Test SNS Property type
    @Test
    public void testType() {
        String type = "String";
        SnsProperties instance = new SnsProperties();
        instance.setType(type);

        assertEquals(Optional.of(instance.getType()), Optional.ofNullable(type));
    }

    //Test SNS Property message
    @Test
    public void testMessage() {
        String message = "SNS Test Message";
        SnsProperties instance = new SnsProperties();
        instance.setMessage(message);

        assertEquals(Optional.of(instance.getMessage()), Optional.ofNullable(message));
    }

    //Test SNS Property region
    @Test
    public void testRegion() {
        String region = "us-east-1";
        SnsProperties instance = new SnsProperties();
        instance.setRegion(region);

        assertEquals(Optional.of(instance.getRegion()), Optional.ofNullable(region));
    }

    //Test SNS Property endpoint
    @Test
    public void testEndpoint() {
        String endpoint = "http://localhost:4575/topic/test_my_topic";
        SnsProperties instance = new SnsProperties();
        instance.setEndpoint(endpoint);

        assertEquals(Optional.of(instance.getEndpoint()), Optional.ofNullable(endpoint));
    }

    //Test SNS Property topicarn
    @Test
    public void testTopicArn() {
        String topicarn = "arn:aws:sns:us-east-1:000000000000:test_my_topic";
        SnsProperties instance = new SnsProperties();
        instance.setTopicArn(topicarn);

        assertEquals(Optional.of(instance.getTopicArn()) , Optional.ofNullable(topicarn));
    }

    //Test SNS Property accessKey
    @Test
    public void testAccessKey() {
        String accessKey = "test_my_key";
        SnsProperties instance = new SnsProperties();
        instance.setAccessKey(accessKey);

        assertEquals(Optional.of(instance.getAccessKey()) , Optional.ofNullable(accessKey));
    }

    //Test SNS Property secretKey
    @Test
    public void testSecretKey() {
        String secretKey = "test_my_secret";
        SnsProperties instance = new SnsProperties();
        instance.setSecretKey(secretKey);

        assertEquals(Optional.of(instance.getSecretKey()) , Optional.ofNullable(secretKey));
    }
}