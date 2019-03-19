package gov.va.ocp.framework.service.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.service.autoconfigure.OcpServiceAutoConfiguration;

import static org.junit.Assert.*;

/**
 * Created by rthota on 8/24/17.
 */
public class OcpServiceAutoConfigurationTest {

    private AnnotationConfigWebApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

   @Test
    public void testWebConfiguration() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        context.register(OcpServiceAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(OcpServiceAutoConfiguration.class));

    }
}
