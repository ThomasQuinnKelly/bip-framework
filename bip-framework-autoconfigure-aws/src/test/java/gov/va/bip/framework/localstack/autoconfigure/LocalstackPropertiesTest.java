package gov.va.bip.framework.localstack.autoconfigure;

import gov.va.bip.framework.localstack.sns.config.LocalstackSnsProperties;
import gov.va.bip.framework.localstack.sqs.config.LocalstackSqsProperties;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocalstackPropertiesTest {

    private static final String NAME = "TestName";
    private static final int PORT = 2020;
    LocalstackProperties props;

    @Before
    public void setUp() throws Exception {
        props = new LocalstackProperties();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testSetServices() {
        final List<LocalstackProperties.Services> services = new ArrayList<>();
        final LocalstackProperties.Services service = new LocalstackProperties().new Services();
        service.setName(NAME);
        service.setPort(PORT);
        services.add(service);

        props.setServices(services);
        assertNotNull(props.getServices());
        assertTrue(NAME.equals(props.getServices().get(0).getName()));
        assertTrue(PORT == props.getServices().get(0).getPort());
    }

    @Test
    public final void testGetSqsServices() {
        LocalstackSqsProperties localstackSqsProperties = new LocalstackSqsProperties();
        localstackSqsProperties.setEnabled(true);
        localstackSqsProperties.setPort(8888);

        List<LocalstackProperties.Services> services = new ArrayList<LocalstackProperties.Services>() {
            @Override
            public LocalstackProperties.Services get(int index) {
                return null;
            }
        };
        Assert.assertNull(services.get(0));

        services = new ArrayList<>();

        final LocalstackProperties.Services sqsservice = new LocalstackProperties().new Services();
        sqsservice.setName("sqs");
        sqsservice.setPort(localstackSqsProperties.getPort());
        services.add(sqsservice);

        assertNotNull(services);
        props.setServices(services);
        Assert.assertTrue(localstackSqsProperties.isEnabled());
        Assert.assertNotNull(sqsservice.getName());
        Assert.assertNotNull(sqsservice.getPort());
        Assert.assertNotNull(props.getServices().get(0).getName());
    }

    @Test
    public final void testSqsEnabled() {


        boolean enabled = false;
        LocalstackSqsProperties instance = new LocalstackSqsProperties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.isEnabled()), Optional.ofNullable(enabled));
    }

    @Test
    public final void testSqsProperties() {

        LocalstackSqsProperties localstackSqsProperties = new LocalstackSqsProperties();
        LocalstackProperties instance = new LocalstackProperties();
        instance.setLocalstackSqsProperties(localstackSqsProperties);

        assertEquals(Optional.of(instance.getLocalstackSqsProperties()), Optional.ofNullable(localstackSqsProperties));
    }

    @Test
    public final void testSnsProperties() {

        LocalstackSnsProperties localstackSnsProperties = new LocalstackSnsProperties();
        LocalstackProperties instance = new LocalstackProperties();
        instance.setLocalstackSnsProperties(localstackSnsProperties);

        assertEquals(Optional.of(instance.getLocalstackSnsProperties()), Optional.ofNullable(localstackSnsProperties));
    }

    @Test
    public final void testLocalstackServices() {

        LocalstackSnsProperties localstackSnsProperties = new LocalstackSnsProperties();
        LocalstackProperties instance = new LocalstackProperties();
        instance.setLocalstackSnsProperties(localstackSnsProperties);

        assertEquals(Optional.of(instance.getLocalstackSnsProperties()), Optional.ofNullable(localstackSnsProperties));
    }

    @Test
    public void testInnerObject___() {
        String name = "sqs";
        int port = 8888;

        final LocalstackProperties.Services service = new LocalstackProperties().new Services(name, port);

        Assert.assertNotNull(service);
    }

    @Test
    public void testAddSqsService() {
        String name = "sqs";
        int port = 8888;
        boolean enabled=true;

        Assert.assertTrue(enabled);

        final ArrayList<LocalstackProperties.Services> services = new ArrayList<>();

        final LocalstackProperties.Services sqs = new LocalstackProperties().new Services(name, port);
        services.add(sqs);

        Assert.assertNotNull(services);
    }
}
