package gov.va.bip.framework.sns.config;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class SnsTopicPropertiesTest {

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
}