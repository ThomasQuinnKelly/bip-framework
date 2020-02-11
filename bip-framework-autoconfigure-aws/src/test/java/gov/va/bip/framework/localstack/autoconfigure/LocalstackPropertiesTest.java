package gov.va.bip.framework.localstack.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public final void testSetGetServices() {
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

}
