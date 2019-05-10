package gov.va.bip.framework.cache.autoconfigure;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class BipCachesConfigTest {

	@Test
	public void getRedisCacheConfigsWithNullExpiresTest() {
		BipCachesConfig config = new BipCachesConfig();
		BipRedisCacheProperties bipRedisCacheProperties = new BipRedisCacheProperties();
		bipRedisCacheProperties.setExpires(null);
		ReflectionTestUtils.setField(config, "bipRedisCacheProperties", bipRedisCacheProperties);
		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> map =
				ReflectionTestUtils.invokeMethod(config, "getRedisCacheConfigs", (Object[]) null);
		assertTrue(map.isEmpty());
	}
}
