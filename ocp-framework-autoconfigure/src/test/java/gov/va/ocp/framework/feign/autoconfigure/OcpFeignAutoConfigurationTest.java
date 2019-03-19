package gov.va.ocp.framework.feign.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

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

import com.netflix.hystrix.HystrixCommand;

import feign.Feign;
import feign.Target;
import feign.hystrix.SetterFactory;
import gov.va.ocp.framework.audit.autoconfigure.OcpAuditAutoConfiguration;
import gov.va.ocp.framework.rest.provider.aspect.ProviderHttpAspect;
import gov.va.ocp.framework.security.autoconfigure.OcpSecurityAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class OcpFeignAutoConfigurationTest {

	private static String CONNECTION_TIMEOUT = "20000";

	private OcpFeignAutoConfiguration ocpFeignAutoConfiguration;

	private AnnotationConfigWebApplicationContext context;

	@Before
	public void setup() {
		context = new AnnotationConfigWebApplicationContext();
		TestPropertyValues.of("feign.hystrix.enabled=true").applyTo(context);
		TestPropertyValues.of("ocp.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
		context.register(JacksonAutoConfiguration.class, SecurityAutoConfiguration.class,
				EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
				OcpSecurityAutoConfiguration.class,
				OcpAuditAutoConfiguration.class, OcpFeignAutoConfiguration.class,
				ProviderHttpAspect.class);

		context.refresh();
		assertNotNull(context);

		ocpFeignAutoConfiguration = context.getBean(OcpFeignAutoConfiguration.class);
		assertNotNull(ocpFeignAutoConfiguration);
	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testWebConfiguration() throws Exception {
		final TokenFeignRequestInterceptor tokenFeignRequestInterceptor = this.context.getBean(TokenFeignRequestInterceptor.class);
		assertNotNull(tokenFeignRequestInterceptor);
	}

	@Test
	public void testWebConfiguration_BrokenProp() throws Exception {
		TestPropertyValues.of("ocp.rest.client.connectionTimeout=BLAHBLAH").applyTo(context);
		context.refresh();

		try {
			ocpFeignAutoConfiguration.feignBuilder();
			fail("ocpFeignAutoConfiguration.feignBuilder() should have thrown IllegalStateException");
		} catch (Exception e) {
			assertTrue(BeansException.class.isAssignableFrom(e.getClass()));
		} finally {
			TestPropertyValues.of("ocp.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
			context.refresh();
		}

	}

	@Test
	public void testGetterSettingReferenceFiegnConfig() throws Exception {
		final OcpFeignAutoConfiguration ocpFeignAutoConfiguration = new OcpFeignAutoConfiguration();
		assertEquals("defaultGroup", ocpFeignAutoConfiguration.getGroupKey());
		ocpFeignAutoConfiguration.setGroupKey("NewGroupKey");
		assertEquals("NewGroupKey", ocpFeignAutoConfiguration.getGroupKey());
	}

	/**
	 * Test of feignBuilder method, of class OcpFeignAutoConfiguration.
	 */
	@Test
	public void testFeignBuilder() {
		final Feign.Builder result = ocpFeignAutoConfiguration.feignBuilder();
		assertNotNull(result);

	}

	@Test
	public void testSetterFactory() {
		final Feign.Builder result = ocpFeignAutoConfiguration.feignBuilder();

		try {
			final Field setterFactoryField = result.getClass().getDeclaredField("setterFactory");
			setterFactoryField.setAccessible(true);
			final SetterFactory factory = (SetterFactory) setterFactoryField.get(result);
			final Target<?> target = new TestTarget(this.getClass(), "testFeignBuilder");
			final HystrixCommand.Setter setter = factory.create(target, this.getClass().getMethod("testFeignBuilder"));
			assertNotNull(setter);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| NoSuchMethodException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}

	@SuppressWarnings("rawtypes")
	class TestTarget extends Target.HardCodedTarget {

		@SuppressWarnings("unchecked")
		public TestTarget(final Class type, final String url) {
			super(type, url);
		}

	}
}
