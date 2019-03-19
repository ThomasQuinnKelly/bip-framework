package gov.va.ocp.framework.audit.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.audit.autoconfigure.OcpAuditAutoConfiguration;

import static org.junit.Assert.*;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
public class OcpAuditAutoConfigurationTest {

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
        context.register(JacksonAutoConfiguration.class, OcpAuditAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(AuditLogSerializer.class));
    }
}
