package gov.va.bip.framework.hystrix.autoconfigure;

import com.netflix.hystrix.strategy.HystrixPlugins;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class HystrixContextAutoConfigurationTest {

	@AfterClass
	public static void resetConfiguration() {
		HystrixPlugins.reset();
	}

	@Test
	public void testObjectCreation() {
		assertNotNull(new HystrixContextAutoConfiguration());
	}

	@Test
	public void testRequestAttributeAwareCallableWrapper() {
		assertNotNull(new HystrixContextAutoConfiguration().requestAttributeAwareCallableWrapper());
	}

	@Test
	public void testConfigureHystrixConcurencyStrategy() {
		List<HystrixCallableWrapper> wrappers = new LinkedList<HystrixCallableWrapper>();
		wrappers.add(new RequestAttributeAwareCallableWrapper());
		HystrixContextAutoConfiguration hystrixContextAutoConfiguration = new HystrixContextAutoConfiguration();
		ReflectionTestUtils.setField(hystrixContextAutoConfiguration, "wrappers", wrappers);
		// no exceptions to be thrown
		try {
			hystrixContextAutoConfiguration.configureHystrixConcurencyStrategy();
		} catch (Exception e) {
			fail("No exception expected when calling method configureHystrixConcurencyStrategy() on hystrixContextAutoConfiguration object");
		}
	}

	@Test
	public void testConfigureHystrixConcurencyStrategyWithEmptyWrappers() {
		List<HystrixCallableWrapper> wrappers = new LinkedList<HystrixCallableWrapper>();
		HystrixContextAutoConfiguration hystrixContextAutoConfiguration = new HystrixContextAutoConfiguration();
		ReflectionTestUtils.setField(hystrixContextAutoConfiguration, "wrappers", wrappers);
		// no exceptions to be thrown
		try {
			hystrixContextAutoConfiguration.configureHystrixConcurencyStrategy();
		} catch (Exception e) {
			fail("No exception expected when calling method configureHystrixConcurencyStrategy() on hystrixContextAutoConfiguration object");
		}
	}

}