package gov.va.bip.framework.localstack.sns.config;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class LocalstackSnsPropertiesTest {

    //Test SNS Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        LocalstackSnsProperties instance = new LocalstackSnsProperties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.isEnabled()), Optional.ofNullable(enabled));
    }

    //Test SNS Property port
    @Test
    public void testPort() {
        int port = 4575;
        LocalstackSnsProperties instance = new LocalstackSnsProperties();
        instance.setPort(port);

        assertEquals(Optional.of(instance.getPort()), Optional.ofNullable(port));
    }
    
}
