package gov.va.bip.framework.cache.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.bip.framework.audit.autoconfigure.BipAuditAutoConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ContextConfiguration(classes = { BipRedisCacheProperties.class, RedisProperties.class })
public class BipJedisConnectionConfigTest {

	private AnnotationConfigApplicationContext context;

	@InjectMocks
	BipJedisConnectionConfig bipJedisConnectionConfig;

	/** The output capture. */
	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	// Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> captorLoggingEvent;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public final void testOnApplicationEvent_HappyPath() {
		context = new AnnotationConfigApplicationContext();
		context.register(RedisAutoConfiguration.class, BipRedisCacheProperties.class,
				BipJedisConnectionConfig.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class, TestConfigurationForAuditBeans.class, BuildProperties.class,
				JedisConnectionFactory.class, TestConfigurationClassForBuildProperties.class);
		context.getBeanFactory().registerScope("refresh", new SimpleThreadScope());
		context.refresh();

		bipJedisConnectionConfig = context.getBean(BipJedisConnectionConfig.class);
		assertNotNull(bipJedisConnectionConfig);
		bipJedisConnectionConfig.onApplicationEvent(new RefreshScopeRefreshedEvent());

		assertTrue(outputCapture.toString().contains("redisConnectionFactory destroyed"));
	}

	@Test
	public final void testOnApplicationEvent_NoFactoryBean() {
		context = Mockito.spy(new AnnotationConfigApplicationContext());
		context.register(RedisAutoConfiguration.class,
				BipRedisCacheProperties.class,
				BipJedisConnectionConfig.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class, TestConfigurationForAuditBeans.class, BuildProperties.class,
				TestConfigurationClassForBuildProperties.class);
		context.getBeanFactory().registerScope("refresh", new SimpleThreadScope());
		context.refresh();

		Mockito.when(context.containsBean(Mockito.anyString())).thenReturn(false);

		bipJedisConnectionConfig = context.getBean(BipJedisConnectionConfig.class);
		assertNotNull(bipJedisConnectionConfig);

		bipJedisConnectionConfig.onApplicationEvent(new RefreshScopeRefreshedEvent());

		assertTrue(outputCapture.toString().contains("does not yet exist"));
	}

	@Test
	public final void testRedisConnectionFactory()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		context = new AnnotationConfigApplicationContext();
		context.register(RedisAutoConfiguration.class,
				BipRedisCacheProperties.class,
				BipJedisConnectionConfig.class, BipCacheAutoConfiguration.class,
				BipAuditAutoConfiguration.class, TestConfigurationForAuditBeans.class, BuildProperties.class,
				TestConfigurationClassForBuildProperties.class);
		context.getBeanFactory().registerScope("refresh", new SimpleThreadScope());
		context.refresh();

		bipJedisConnectionConfig = context.getBean(BipJedisConnectionConfig.class);
		assertNotNull(bipJedisConnectionConfig);

		bipJedisConnectionConfig.redisConnectionFactory();
		assertTrue(!outputCapture.toString().contains("poolConfig:"));

		RedisProperties props = bipJedisConnectionConfig.redisProperties;
		RedisProperties.Pool pool = new RedisProperties.Pool();
		pool.setMaxActive(1000);
		pool.setMaxIdle(1000);
		pool.setMaxWait(Duration.ofMillis(1000L));
		pool.setMinIdle(1000);
		props.getJedis().setPool(pool);

		bipJedisConnectionConfig.redisConnectionFactory();
		assertTrue(outputCapture.toString().contains("MaxIdle=1000"));

		props = bipJedisConnectionConfig.redisProperties;
		props.setSsl(true);
		props.setTimeout(Duration.ofSeconds(0L));
		pool = new RedisProperties.Pool();
		pool.setMaxActive(0);
		pool.setMaxIdle(0);
		pool.setMaxWait(null);
		pool.setMinIdle(0);
		props.getJedis().setPool(pool);

		bipJedisConnectionConfig.redisConnectionFactory();
		assertTrue(outputCapture.toString().contains("MinIdle=0"));
	}

}
