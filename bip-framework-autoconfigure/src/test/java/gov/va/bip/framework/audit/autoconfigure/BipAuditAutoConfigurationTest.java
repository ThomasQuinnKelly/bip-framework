package gov.va.bip.framework.audit.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.autoconfigure.BipAuditAutoConfiguration;

import static org.junit.Assert.*;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

public class BipAuditAutoConfigurationTest {

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
        context.register(JacksonAutoConfiguration.class, BipAuditAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(AuditLogSerializer.class));
    }
}
