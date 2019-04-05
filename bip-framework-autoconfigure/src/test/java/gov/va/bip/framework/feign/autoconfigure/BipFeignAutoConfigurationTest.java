package gov.va.bip.framework.feign.autoconfigure;

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
import gov.va.bip.framework.audit.autoconfigure.BipAuditAutoConfiguration;
import gov.va.bip.framework.feign.autoconfigure.BipFeignAutoConfiguration;
import gov.va.bip.framework.feign.autoconfigure.TokenFeignRequestInterceptor;
import gov.va.bip.framework.rest.provider.aspect.ProviderHttpAspect;
import gov.va.bip.framework.security.autoconfigure.BipSecurityAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BipFeignAutoConfigurationTest {

	private static String CONNECTION_TIMEOUT = "20000";

	private BipFeignAutoConfiguration bipFeignAutoConfiguration;

	private AnnotationConfigWebApplicationContext context;

	@Before
	public void setup() {
		context = new AnnotationConfigWebApplicationContext();
		TestPropertyValues.of("feign.hystrix.enabled=true").applyTo(context);
		TestPropertyValues.of("bip.framework.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
		context.register(JacksonAutoConfiguration.class, SecurityAutoConfiguration.class,
				EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
				BipSecurityAutoConfiguration.class,
				BipAuditAutoConfiguration.class, BipFeignAutoConfiguration.class,
				ProviderHttpAspect.class);

		context.refresh();
		assertNotNull(context);

		bipFeignAutoConfiguration = context.getBean(BipFeignAutoConfiguration.class);
		assertNotNull(bipFeignAutoConfiguration);
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
		TestPropertyValues.of("bip.framework.rest.client.connectionTimeout=BLAHBLAH").applyTo(context);
		context.refresh();

		try {
			bipFeignAutoConfiguration.feignBuilder();
			fail("bipFeignAutoConfiguration.feignBuilder() should have thrown IllegalStateException");
		} catch (Exception e) {
			assertTrue(BeansException.class.isAssignableFrom(e.getClass()));
		} finally {
			TestPropertyValues.of("bip.framework.rest.client.connectionTimeout=" + CONNECTION_TIMEOUT).applyTo(context);
			context.refresh();
		}

	}

	@Test
	public void testGetterSettingReferenceFiegnConfig() throws Exception {
		final BipFeignAutoConfiguration bipFeignAutoConfiguration = new BipFeignAutoConfiguration();
		assertEquals("defaultGroup", bipFeignAutoConfiguration.getGroupKey());
		bipFeignAutoConfiguration.setGroupKey("NewGroupKey");
		assertEquals("NewGroupKey", bipFeignAutoConfiguration.getGroupKey());
	}

	/**
	 * Test of feignBuilder method, of class BipFeignAutoConfiguration.
	 */
	@Test
	public void testFeignBuilder() {
		final Feign.Builder result = bipFeignAutoConfiguration.feignBuilder();
		assertNotNull(result);

	}

	@Test
	public void testSetterFactory() {
		final Feign.Builder result = bipFeignAutoConfiguration.feignBuilder();

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
