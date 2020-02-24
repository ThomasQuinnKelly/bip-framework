package gov.va.bip.framework.security.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.bip.framework.security.opa.BipOpaProperties;

/**
 * Created by vgadda on 7/31/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ContextConfiguration(classes = { BipOpaProperties.class })
public class ReferenceSecurityAutoConfigurationTests {

    private AnnotationConfigWebApplicationContext context;
    
    private static final String BIP_FRAMEWORK_OPA_ENABLED = "bip.framework.security.opa.enabled=true";
    private static final String BIP_FRAMEWORK_OPA_URLS = "bip.framework.security.opa.urls[0]=http://localhost:8080/api/v1/mytest/pid";
    private static final String BIP_FRAMEWORK_OPA_URLS_INVALID = "bip.framework.security.opa.urls[0]=";

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testWebConfiguration() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        context.register(SecurityAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class, BipSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(4, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
    
    @Test
    public void testWebConfigurationOpaEnabled() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        TestPropertyValues.of(BIP_FRAMEWORK_OPA_ENABLED).applyTo(context);
        context.register(SecurityAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class, BipSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(4, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
    
    @Test
    public void testWebConfigurationOpaEnabledWithUrls() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        TestPropertyValues.of(BIP_FRAMEWORK_OPA_ENABLED).applyTo(context);
        TestPropertyValues.of(BIP_FRAMEWORK_OPA_URLS).applyTo(context);
        context.register(SecurityAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class, BipSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(4, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
    
    @Test
    public void testWebConfigurationOpaEnabledWithInvalidUrls() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        TestPropertyValues.of(BIP_FRAMEWORK_OPA_ENABLED).applyTo(context);
        TestPropertyValues.of(BIP_FRAMEWORK_OPA_URLS_INVALID).applyTo(context);
        context.register(SecurityAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class, BipSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(4, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
}
