package gov.va.bip.framework.localstack.sqs.config;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class LocalstacksqsPropertiesTest {

    //Test SQS Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        LocalstackSqsProperties instance = new LocalstackSqsProperties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.isEnabled()), Optional.ofNullable(enabled));
    }

    //Test SQS Property port
    @Test
    public void testPort() {
        int port = 4575;
        LocalstackSqsProperties instance = new LocalstackSqsProperties();
        instance.setPort(port);

        assertEquals(Optional.of(instance.getPort()), Optional.ofNullable(port));
    }

}
