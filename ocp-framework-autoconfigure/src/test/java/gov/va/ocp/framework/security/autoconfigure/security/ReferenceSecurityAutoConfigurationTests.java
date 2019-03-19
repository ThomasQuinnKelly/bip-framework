package gov.va.ocp.framework.security.autoconfigure.security;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.security.autoconfigure.OcpSecurityAutoConfiguration;

import static org.junit.Assert.*;

/**
 * Created by vgadda on 7/31/17.
 */
public class ReferenceSecurityAutoConfigurationTests {

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
        context.register(SecurityAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class, OcpSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(4, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
}
