package gov.va.ocp.framework.service.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.audit.autoconfigure.OcpAuditAutoConfiguration;

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
		context.register(OcpAuditAutoConfiguration.class);
		context.register(OcpServiceAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		assertNotNull(this.context.getBean(OcpAuditAutoConfiguration.class));
		assertNotNull(this.context.getBean(OcpServiceAutoConfiguration.class));

	}
}
