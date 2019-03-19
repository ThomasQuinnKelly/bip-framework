package gov.va.ocp.framework.cache.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;

@ConfigurationProperties(prefix = "ocp.cache")
@Configuration
public class OcpCacheProperties {

	static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(OcpCacheProperties.class);

	private List<RedisExpires> expires;

	private Long defaultExpires = 86400L;

	/** redis config properties */
	private RedisConfig redisConfig;

	public void setExpires(final List<RedisExpires> expires) {
		this.expires = expires;
	}

	public void setDefaultExpires(final Long defaultExpires) {
		this.defaultExpires = defaultExpires;
	}

	public void setRedisConfig(final RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}

	public List<RedisExpires> getExpires() {
		return this.expires;
	}

	public Long getDefaultExpires() {
		return this.defaultExpires;
	}

	public RedisConfig getRedisConfig() {
		return this.redisConfig;
	}

	/**
	 * Inner class to hold the time to live (ttl) for a given cache name.
	 */
	public static class RedisExpires {

		/** Redis Host */
		private String name;

		/** Redis port */
		private Long ttl;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Long getTtl() {
			return ttl;
		}

		public void setTtl(final Long ttl) {
			this.ttl = ttl;
		}
	}

	/**
	 * Inner class for a redis host and the port it runs on.
	 */
	public static class RedisConfig {

		/** Redis Host */
		private String host;

		/** Redis port */
		private Integer port;

		public void setHost(final String host) {
			this.host = host;
		}

		public String getHost() {
			return host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(final Integer port) {
			this.port = port;
		}

	}

}
