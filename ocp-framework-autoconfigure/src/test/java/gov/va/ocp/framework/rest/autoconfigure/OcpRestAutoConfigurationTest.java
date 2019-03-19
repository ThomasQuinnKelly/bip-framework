package gov.va.ocp.framework.rest.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.audit.autoconfigure.OcpAuditAutoConfiguration;
import gov.va.ocp.framework.rest.provider.aspect.ProviderHttpAspect;
import gov.va.ocp.framework.security.autoconfigure.OcpSecurityAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class OcpRestAutoConfigurationTest {

	private static final String CONNECTION_TIMEOUT = "20000";

	private OcpRestAutoConfiguration ocpRestAutoConfiguration;

	private AnnotationConfigWebApplicationContext context;

	@Before
	public void setup() {
		context = new AnnotationConfigWebApplicationContext();
		TestPropertyValues.of("feign.hystrix.enabled=true").applyTo(context);
		TestPropertyValues.of("ocp.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
		context.register(JacksonAutoConfiguration.class, SecurityAutoConfiguration.class,
				EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
				OcpSecurityAutoConfiguration.class,
				OcpAuditAutoConfiguration.class, OcpRestAutoConfiguration.class,
				ProviderHttpAspect.class);

		context.refresh();
		assertNotNull(context);

		// test configuration and give ocpRestAutoConfiguration a value for other tests
		ocpRestAutoConfiguration = context.getBean(OcpRestAutoConfiguration.class);
		assertNotNull(ocpRestAutoConfiguration);
	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testConfiguration_Broken() {
		TestPropertyValues.of("ocp.rest.client.connectionTimeout=BLAHBLAH").applyTo(context);

		try {
			context.refresh();
			ocpRestAutoConfiguration.restClientTemplate();
			fail("OcpRestAutoConfiguration should have thrown IllegalStateException or BeansException");
		} catch (Exception e) {
			assertTrue(BeansException.class.isAssignableFrom(e.getClass()));
		} finally {
			TestPropertyValues.of("ocp.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
			context.refresh();
			ocpRestAutoConfiguration = context.getBean(OcpRestAutoConfiguration.class);
			assertNotNull(ocpRestAutoConfiguration);
		}
	}

	@Test
	public void testWebConfiguration() throws Exception {
		assertNotNull(ocpRestAutoConfiguration.providerHttpAspect());
		assertNotNull(ocpRestAutoConfiguration.restProviderTimerAspect());
		assertNotNull(ocpRestAutoConfiguration.restClientTemplate());
		assertNotNull(ocpRestAutoConfiguration.tokenClientHttpRequestInterceptor());
	}

}
