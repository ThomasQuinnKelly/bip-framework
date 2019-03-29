package gov.va.ocp.framework.cache.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ocp.framework.audit.autoconfigure.OcpAuditAutoConfiguration;

/**
 * Created by vgadda on 8/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class OcpCacheAutoConfigurationTest {

	private static final String SPRING_CACHE_TYPE_PROPERTY_AND_VALUE = "spring.cache.type=redis";

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
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		CacheManager cacheManager = context.getBean(CacheManager.class);
		assertNotNull(cacheManager);
	}

	@Test
	public void testReferenceCacheConfigurations() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		TestPropertyValues.of("ocp.cache.defaultExpires=86401").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheProperties.class);
		context.refresh();
		OcpCacheProperties ocpCacheProperties = context.getBean(OcpCacheProperties.class);
		OcpCacheProperties.RedisExpires redisExpires = new gov.va.ocp.framework.cache.autoconfigure.OcpCacheProperties.RedisExpires();
		redisExpires.setName("testName");
		redisExpires.setTtl(1500L);
		List<OcpCacheProperties.RedisExpires> expiresList = new LinkedList<OcpCacheProperties.RedisExpires>();
		expiresList.add(redisExpires);
		ocpCacheProperties.setExpires(expiresList);
		context.register(OcpCacheAutoConfiguration.class);
		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);

		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> cacheConfigs =
				ocpCacheAutoConfiguration.redisCacheConfigurations();

		assertNotNull(cacheConfigs);
		assertEquals(cacheConfigs.get("testName").getTtl().getSeconds(), 1500L);
	}

	@Test
	public void testReferenceCacheConfigurationKeyGenerator() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
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
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheGetError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCachePutError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCachePutError(new RuntimeException("Test Message"), mockCache, "TestKey",
				"TestValue");
	}

	@Test
	public void testCacheEvictError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheEvictError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCacheClearError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of(SPRING_CACHE_TYPE_PROPERTY_AND_VALUE).applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class, OcpAuditAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheClearError(new RuntimeException("Test Message"), mockCache);
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}
}
