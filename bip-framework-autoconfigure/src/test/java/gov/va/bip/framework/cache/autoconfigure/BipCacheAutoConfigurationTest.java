package gov.va.bip.framework.cache.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.audit.autoconfigure.BipAuditAutoConfiguration;

/**
 * Created by vgadda on 8/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ContextConfiguration(classes = { BipRedisCacheProperties.class, RedisProperties.class })
public class BipCacheAutoConfigurationTest {

	private static final String SPRING_CACHE_TYPE_PROPERTY_AND_VALUE = "spring.cache.type=redis";
	private static final String BIP_REDIS_CLIENT_NAME = "bip.framework.redis.client.clientName=redis_test-client-name";

	private AnnotationConfigApplicationContext context;

	@Mock
	CacheManager cacheManager;

	@Mock
	Cache mockCache;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testReferenceCacheConfiguration() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		TestPropertyValues.of(BIP_REDIS_CLIENT_NAME).applyTo(context);

		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class, TestConfigurationForAuditBeans.class, BuildProperties.class,
				TestConfigurationClassForBuildProperties.class);

		context.refresh();
		assertNotNull(context);
		CacheManager cacheManager = context.getBean(CacheManager.class);
		assertNotNull(cacheManager);
	}

	@Test
	public void testReferenceCacheConfigurations() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		TestPropertyValues.of(BIP_REDIS_CLIENT_NAME).applyTo(context);
		TestPropertyValues.of("bip.framework.cache.defaultExpires=86401").applyTo(context);
		TestPropertyValues.of("bip.framework.cache.expires[0].name=testName").applyTo(context);
		TestPropertyValues.of("bip.framework.cache.expires[0].ttl=1500").applyTo(context);
		context.registerBean(RedisAutoConfiguration.class, JedisConnectionFactory.class, BipCacheAutoConfiguration.class,
				BipRedisCacheProperties.class);
		context.register(RedisAutoConfiguration.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, TestConfigurationClassForBuildProperties.class, RefreshScope.class,
				BuildProperties.class);
		context.refresh();
		BipCachesConfig bipCachesConfig = context.getBean(BipCachesConfig.class);
		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> cacheConfigs =
				ReflectionTestUtils.invokeMethod(bipCachesConfig, "getRedisCacheConfigs");

		assertNotNull(cacheConfigs);
		assertEquals(1500L, cacheConfigs.get("testName").getTtl().getSeconds());
	}

	@Test
	public void testReferenceCacheConfigurationKeyGenerator() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, BuildProperties.class, TestConfigurationClassForBuildProperties.class);
		context.refresh();
		assertNotNull(context);
		KeyGenerator keyGenerator = context.getBean(KeyGenerator.class);
		String key = (String) keyGenerator.generate(new Object(), myMethod(), new Object());
		assertNotNull(key);
	}

	@Test
	public void testCacheGetError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, BuildProperties.class, TestConfigurationClassForBuildProperties.class);
		context.refresh();
		assertNotNull(context);

		BipCachesConfig bipCacheAutoConfiguration = context.getBean(BipCachesConfig.class);
		bipCacheAutoConfiguration.errorHandler().handleCacheGetError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCachePutError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, BuildProperties.class, TestConfigurationClassForBuildProperties.class);
		context.refresh();
		assertNotNull(context);

		BipCachesConfig bipCacheAutoConfiguration = context.getBean(BipCachesConfig.class);
		bipCacheAutoConfiguration.errorHandler().handleCachePutError(new RuntimeException("Test Message"), mockCache, "TestKey",
				"TestValue");
	}

	@Test
	public void testCacheEvictError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.registerBean(BaseAsyncAudit.class);
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, BuildProperties.class, TestConfigurationClassForBuildProperties.class);
		context.refresh();
		assertNotNull(context);

		BipCachesConfig bipCacheAutoConfiguration = context.getBean(BipCachesConfig.class);
		bipCacheAutoConfiguration.errorHandler().handleCacheEvictError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCacheClearError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.registerBean(BaseAsyncAudit.class);
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class,
				TestConfigurationForAuditBeans.class, BuildProperties.class, TestConfigurationClassForBuildProperties.class);
		context.refresh();
		assertNotNull(context);

		BipCachesConfig bipCacheAutoConfiguration = context.getBean(BipCachesConfig.class);
		bipCacheAutoConfiguration.errorHandler().handleCacheClearError(new RuntimeException("Test Message"), mockCache);
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}

}

@Configuration
class TestConfigurationClassForBuildProperties {

	@ConditionalOnMissingBean
	@Bean
	public BuildProperties buildProperties() throws Exception {
		Properties prop = new Properties();
		prop.load(this.getClass().getClassLoader().getResourceAsStream("META-INF/build-info.properties"));
		return new BuildProperties(prop);
	};
}