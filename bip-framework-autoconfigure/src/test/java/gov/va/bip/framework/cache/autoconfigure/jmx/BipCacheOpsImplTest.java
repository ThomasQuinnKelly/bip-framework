package gov.va.bip.framework.cache.autoconfigure.jmx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import redis.clients.jedis.JedisPoolConfig;

@RunWith(SpringRunner.class)
public class BipCacheOpsImplTest {

	/** Prefix for log statements */
	private static final String PREFIX = ":::: ";

	/** The output capture. */
	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	@SuppressWarnings("rawtypes")
	@Mock
	private ch.qos.logback.core.Appender mockAppender;

	@Mock
	private RedisCacheManager cacheManager;

	BipCacheOpsImpl bipCacheOpsImpl;

	@Before
	public void setup() {
		bipCacheOpsImpl = new BipCacheOpsImpl();
		assertNotNull(bipCacheOpsImpl);
	}

	@Test
	public void testClearAllCaches() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field cm = bipCacheOpsImpl.getClass().getDeclaredField("cacheManager");
		cm.setAccessible(true);
		cm.set(bipCacheOpsImpl, cacheManager);

		bipCacheOpsImpl.clearAllCaches();
	}

	@Test
	public void testClearAllCachesNoCache()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field cm = bipCacheOpsImpl.getClass().getDeclaredField("cacheManager");
		cm.setAccessible(true);
		cm.set(bipCacheOpsImpl, null);

		bipCacheOpsImpl.clearAllCaches();
	}

	@Test
	public void testlogCacheConfigProperties() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
		ReflectionTestUtils.setField(redisCacheConfiguration, "ttl", Duration.ofSeconds(1500L));
		ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfiguration", redisCacheConfiguration);
		Properties properties = new Properties();
		properties.setProperty("testKey", "testValue");
		BuildProperties buildProperties = new BuildProperties(properties);
		Map<String, RedisCacheConfiguration> redisCacheConfigurations = new HashMap<String, RedisCacheConfiguration>();
		redisCacheConfigurations.put("testKeyForConfig", redisCacheConfiguration);
		ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfigurations", redisCacheConfigurations);
		RedisCacheManager mockCacheManager = mock(RedisCacheManager.class);
		when(mockCacheManager.getCacheNames()).thenReturn(Arrays.asList(new String[] { "cacheName1" }));
		ReflectionTestUtils.setField(bipCacheOpsImpl, "cacheManager", mockCacheManager);

		ReflectionTestUtils.setField(bipCacheOpsImpl, "buildProperties", buildProperties);
		bipCacheOpsImpl.logCacheConfigProperties();
		String outString = outputCapture.toString();

		assertTrue(outString.contains("Cache Configs in '" + buildProperties.getName() + "'"));
		assertTrue(outString.contains("Config for Default: [TTL=" + redisCacheConfiguration.getTtl().toMillis()));
		assertTrue(outString.contains(redisCacheConfiguration.getConversionService().getClass().getName()));
		assertTrue(outString.contains(PREFIX + "Config for [key="));
		assertTrue(outString.contains(PREFIX + "Cache names in CacheManager: [" + "cacheName1" + "]"));
	}

	@Test
	public void testlogCacheConfigPropertiesWithNullValues() {
		try {
			BipCacheOpsImpl bipCacheOpsImpl = new BipCacheOpsImpl();
			RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
			ReflectionTestUtils.setField(redisCacheConfiguration, "ttl", null);
			ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfiguration", redisCacheConfiguration);
			ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfigurations", null);
			RedisCacheManager mockCacheManager = mock(RedisCacheManager.class);
			when(mockCacheManager.getCacheNames()).thenReturn(Arrays.asList(new String[] {}));
			ReflectionTestUtils.setField(bipCacheOpsImpl, "cacheManager", mockCacheManager);
			bipCacheOpsImpl.logCacheConfigProperties();

			Map<String, RedisCacheConfiguration> redisCacheConfigurations = new HashMap<String, RedisCacheConfiguration>();
			ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfigurations", redisCacheConfigurations);
			bipCacheOpsImpl.logCacheConfigProperties();
		} catch (Exception e) {
			fail("Exception should not be thrown");
		}
	}

	@Test
	public void testlogCurrentJedisConnectionFactoryFields() {
		JedisConnectionFactory mockRedisConnectionFactory = mock(JedisConnectionFactory.class);

		RedisStandaloneConfiguration mockRsc = mock(RedisStandaloneConfiguration.class);
		when(mockRedisConnectionFactory.getStandaloneConfiguration()).thenReturn(mockRsc);

		JedisClientConfiguration MockJcc = mock(JedisClientConfiguration.class);
		when(MockJcc.getConnectTimeout()).thenReturn(Duration.ofSeconds(1500L));
		when(MockJcc.getReadTimeout()).thenReturn(Duration.ofSeconds(1500L));
		when(mockRedisConnectionFactory.getClientConfiguration()).thenReturn(MockJcc);

		GenericObjectPoolConfig<JedisPoolConfig> gopc = new GenericObjectPoolConfig<>();
		@SuppressWarnings("unchecked")
		GenericObjectPoolConfig<JedisPoolConfig> mockGopc = mock(gopc.getClass());
		when(mockRedisConnectionFactory.getPoolConfig()).thenReturn(mockGopc);

		ReflectionTestUtils.setField(bipCacheOpsImpl, "redisConnectionFactory", mockRedisConnectionFactory);
		bipCacheOpsImpl.logCurrentJedisConnectionFactoryFields();
		String outString = outputCapture.toString();

		assertTrue(outString.contains(PREFIX + "    clientName = "));
		verify(mockRsc, times(1)).getDatabase();
		verify(mockRsc, times(1)).getHostName();
		verify(mockRsc, times(1)).getPort();

		verify(MockJcc, times(1)).getClientName();
		verify(MockJcc, times(1)).getConnectTimeout();
		verify(MockJcc, times(1)).getHostnameVerifier();
		verify(MockJcc, times(2)).getPoolConfig();
		verify(MockJcc, times(1)).getReadTimeout();

		verify(mockGopc, times(1)).getEvictionPolicyClassName();
		verify(mockGopc, times(1)).getEvictorShutdownTimeoutMillis();
		verify(mockGopc, times(1)).getMaxIdle();
		verify(mockGopc, times(1)).getMaxTotal();
		verify(mockGopc, times(1)).getMaxWaitMillis();
		verify(mockGopc, times(1)).getMinEvictableIdleTimeMillis();
		verify(mockGopc, times(1)).getMinIdle();
		verify(mockGopc, times(1)).getNumTestsPerEvictionRun();
		verify(mockGopc, times(1)).getSoftMinEvictableIdleTimeMillis();
		verify(mockGopc, times(1)).getTimeBetweenEvictionRunsMillis();
		verify(mockGopc, times(1)).getBlockWhenExhausted();
		verify(mockGopc, times(1)).getFairness();
		verify(mockGopc, times(1)).getLifo();
		verify(mockGopc, times(1)).getTestOnBorrow();
		verify(mockGopc, times(1)).getTestOnCreate();
		verify(mockGopc, times(1)).getTestOnReturn();
		verify(mockGopc, times(1)).getTestWhileIdle();
	}

	@Test
	public void testlogCurrentJedisConnectionFactoryFieldsWithNullValues() {
		try {
			ReflectionTestUtils.setField(bipCacheOpsImpl, "redisConnectionFactory", null);
			bipCacheOpsImpl.logCurrentJedisConnectionFactoryFields();
		} catch (Exception e) {
			fail("Exception should not be thrown");
		}
	}

	@Test
	public void testlogCurrentCacheManagerFields() {
		RedisCacheManager mockCacheManager = mock(RedisCacheManager.class);
		String cacheName = "cacheName1";
		when(mockCacheManager.getCacheNames()).thenReturn(Arrays.asList(new String[] { cacheName }));
		RedisCache mockCache = mock(RedisCache.class);
		RedisCacheConfiguration mockConfig = mock(RedisCacheConfiguration.class);
		when(mockCache.getCacheConfiguration()).thenReturn(mockConfig);
		when(mockConfig.getTtl()).thenReturn(Duration.ofSeconds(1500L));
		when(mockCacheManager.isTransactionAware()).thenReturn(false);
		when(mockCacheManager.getCache(cacheName)).thenReturn(mockCache);
		RedisCacheConfiguration mockRedisCacheConfiguration = mock(RedisCacheConfiguration.class);

		ReflectionTestUtils.setField(bipCacheOpsImpl, "cacheManager", mockCacheManager);
		ReflectionTestUtils.setField(bipCacheOpsImpl, "redisCacheConfiguration", mockRedisCacheConfiguration);

		bipCacheOpsImpl.logCurrentCacheManagerFields();
		String outString = outputCapture.toString();

		assertTrue(outString.contains(PREFIX + "RedisCacheManager = "));
		assertTrue(outString.contains(PREFIX + "RedisCacheConfiguration (immutable) = "));
		assertTrue(outString.contains(PREFIX + "Caches = "));

		verify(mockRedisCacheConfiguration, times(1)).getAllowCacheNullValues();
		verify(mockRedisCacheConfiguration, times(1)).getKeySerializationPair();
		verify(mockRedisCacheConfiguration, times(1)).getValueSerializationPair();
		verify(mockRedisCacheConfiguration, times(1)).getConversionService();
		verify(mockRedisCacheConfiguration, times(1)).usePrefix();

		verify(mockCache, times(1)).getCacheConfiguration();
		verify(mockConfig, times(1)).getAllowCacheNullValues();
		verify(mockConfig, times(1)).getTtl();
		verify(mockConfig, times(1)).getKeySerializationPair();
		verify(mockConfig, times(1)).getValueSerializationPair();
		verify(mockConfig, times(1)).getConversionService();
		verify(mockConfig, times(1)).usePrefix();

	}

}
