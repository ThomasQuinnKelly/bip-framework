package gov.va.bip.framework.service.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.bip.framework.audit.autoconfigure.BipAuditAutoConfiguration;
import gov.va.bip.framework.service.autoconfigure.BipServiceAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
public class BipServiceAutoConfigurationTest {

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
		context.register(BipAuditAutoConfiguration.class);
		context.register(BipServiceAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		assertNotNull(this.context.getBean(BipAuditAutoConfiguration.class));
		assertNotNull(this.context.getBean(BipServiceAutoConfiguration.class));

	}
}
