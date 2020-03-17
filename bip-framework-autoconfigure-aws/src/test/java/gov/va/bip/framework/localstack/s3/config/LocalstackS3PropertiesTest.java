package gov.va.bip.framework.localstack.s3.config;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class LocalstackS3PropertiesTest {

    //Test S3 Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        LocalstackS3Properties instance = new LocalstackS3Properties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.isEnabled()), Optional.ofNullable(enabled));
    }

    //Test S3 Property port
    @Test
    public void testPort() {
        int port = 4572;
        LocalstackS3Properties instance = new LocalstackS3Properties();
        instance.setPort(port);

        assertEquals(Optional.of(instance.getPort()), Optional.ofNullable(port));
    }
    
}
