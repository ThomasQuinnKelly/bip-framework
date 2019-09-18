package gov.va.bip.framework.cache.autoconfigure;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Imported by {@link BipCacheAutoConfiguration} so it can participate in the autoconfiguration bootstrap.
 * <p>
 * Configures:
 * <ul>
 * <li> RedisStandaloneConfiguration - the redis "Standalone" module (host, port, db index, password)
 * <li> JedisClientConfiguration (timeouts, connection pool, SSL)
 * </ul>
 *
 * @author aburkholder
 */
@Component
public class BipJedisConnectionConfig {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipJedisConnectionConfig.class);

	private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

	/** Build properties to provide a unique "clientName" for the JedisClientConfiguration */
	@Autowired
	BuildProperties buildProperties;

	/** Properties from the application YAML */
	@Autowired
	RedisProperties redisProperties;

	/** Reference to the Spring Context. Need this in order to get direct access bean refs. */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Ensure autowiring succeeded.
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(buildProperties, BuildProperties.class.getSimpleName() + " cannot be null.");
		Defense.notNull(redisProperties, RedisProperties.class.getSimpleName() + " cannot be null.");
	}

	/**
	 * On the RefreshScope refresh event, destroy the JedisConnectionFactory to force it
	 * to rebuild from {@link #redisConnectionFactory()} with the current setting from the application YAML.
	 * <p>
	 * This event listener <b>must</b> run <b>before</b> any other cache related event listeners.
	 *
	 * @param event the refresh event
	 */
	@EventListener
	@Order(BipCacheAutoConfiguration.REFRESH_ORDER_CONNECTION_FACTORY)
	public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
		LOGGER.debug("Event activated to reconfigure " + REDIS_CONNECTION_FACTORY_BEAN_NAME + ": event.getName() {}",
				event.getName() + "; event.getSource() {}",
				event.getSource());

		if (!applicationContext.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)) {
			LOGGER.debug(REDIS_CONNECTION_FACTORY_BEAN_NAME + " does not yet exist.");
		} else {
			// connection factory is manipulate through the bean factory
			// this is required to allow bean dependencies, autowiring, etc to be reinitialized
			JedisConnectionFactory jedisConnectionFactory =
					(JedisConnectionFactory) applicationContext.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);

			// block on the context, so that in-process executions are not interrupted
			synchronized (applicationContext) {
				// automatically calls any destroy method on the bean
				jedisConnectionFactory.destroy();
			}

			LOGGER.debug(REDIS_CONNECTION_FACTORY_BEAN_NAME + " destroyed.");
		}
	}

	/**
	 * Replaces the default RedisConnectionFactory in the spring context.
	 * This bean configures from the application YAML, and will rebuild the
	 * Redis Connection after {@link #onApplicationEvent(RefreshScopeRefreshedEvent)}
	 * destroys the previous factory.
	 *
	 * @return a connection factory with current property values
	 */
	@RefreshScope
	@Order(1)
	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		String msg = this.getClass() + ".redisConnectionFactory build with ["
				+ "RedisStandaloneConfiguration[Database=" + redisProperties.getDatabase()
				+ ";HostName=" + redisProperties.getHost()
				+ ";Password=" + redisProperties.getPassword()
				+ ";Port=" + redisProperties.getPort()
				+ "]; JedisClientConfiguration["
				+ "clientName=" + buildProperties.getName() + "_" + buildProperties.getVersion()
				+ ";connectTimeout=" + (redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout())
				+ ";readTimeout=" + (redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout());
		if (redisProperties.getJedis().getPool() != null) {
			msg += ";poolConfig: [maxTotal=" + getAppropriateMaxActive()
					+ ";MaxIdle=" + getAppropriateMaxIdleValue()
					+ ";MinIdle=" + getAppropriateMinIdleValue()
					+ ";MaxWaitMillis=" + getAppropriateMaxWaitMillis();
		}
		msg += "]; SSL[" + redisProperties.isSsl()
				+ (redisProperties.isSsl() ? ";hostnameVerifier=NoopHostnameVerifier" : "")
				+ "]]";
		LOGGER.debug(msg);

		return new JedisConnectionFactory(getRedisStandaloneConfiguration(), getJedisClientConfiguration());
	}

	private int getAppropriateMaxActive() {
		return redisProperties.getJedis().getPool().getMaxActive() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_TOTAL
				: redisProperties.getJedis().getPool().getMaxActive();
	}

	private long getAppropriateMaxWaitMillis() {
		return redisProperties.getJedis().getPool().getMaxWait() == null
				? GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS
				: redisProperties.getJedis().getPool().getMaxWait().toMillis();
	}

	private int getAppropriateMinIdleValue() {
		return redisProperties.getJedis().getPool().getMinIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MIN_IDLE
				: redisProperties.getJedis().getPool().getMinIdle();
	}

	private int getAppropriateMaxIdleValue() {
		return redisProperties.getJedis().getPool().getMaxIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_IDLE
				: redisProperties.getJedis().getPool().getMaxIdle();
	}

	/**
	 * Get a Jedis Client config with properties as currently declared in the application YAML.
	 *
	 * @return configuration object for the Jedis Client
	 */
	private JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfigurationBuilder builder = JedisClientConfiguration
				.builder()
				.clientName(buildProperties.getName() + "_" + buildProperties.getVersion())
				.connectTimeout((redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout()))
				.readTimeout((redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout()));

		if (redisProperties.getJedis().getPool() != null) {
			builder.usePooling()
					.poolConfig(jedisPoolConfig(redisProperties.getJedis().getPool()));
		}
		if (redisProperties.isSsl()) {
			builder.useSsl()
					.hostnameVerifier(NoopHostnameVerifier.INSTANCE);
			/* also could set: .sslParameters(..) and sslSocketFactory(..) */
		}
		return builder.build();
	}

	/**
	 * Get a Jedis pool config with properties as currently declared in the application YAML.
	 * <p>
	 * This method is stolen from {@code org.springframework.boot.autoconfigure.data.redis.JedisConnectionConfiguration}
	 * with the assumption that this is the correct way to configure the values
	 * required by Jedis.
	 *
	 * @param pool the RedisProperties.getJedis().getPool()
	 * @return configuration object for the Jedis pool
	 */
	private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal((pool.getMaxActive() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_TOTAL
				: pool.getMaxActive()));
		config.setMaxIdle((pool.getMaxIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_IDLE
				: pool.getMaxIdle()));
		config.setMinIdle((pool.getMinIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MIN_IDLE
				: pool.getMinIdle()));
		config.setMaxWaitMillis((pool.getMaxWait() == null
				? GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS
				: pool.getMaxWait().toMillis()));
		return config;
	}

	/**
	 * Get a Jedis connection configuration object with properties as currently declared in the application YAML.
	 *
	 * @return the standalone configuration
	 */
	private RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
		redisStandaloneConfiguration.setHostName(redisProperties.getHost());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		redisStandaloneConfiguration.setPort(redisProperties.getPort());
		return redisStandaloneConfiguration;
	}
}
