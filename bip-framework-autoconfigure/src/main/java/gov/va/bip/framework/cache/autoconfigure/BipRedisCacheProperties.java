package gov.va.bip.framework.cache.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * Properties used to configure the Redis Cache.
 *
 * Properties used to configure the Redis "Standalone" module, and the EmbeddedRedisServer.
 * <p>
 * For Redis "Standalone" and EmbeddedRedisServer configuration see {@link BipRedisProperties}.<br/>
 * For Redis Client configuration, see {@link BipRedisClientProperties}.<br/>
 * <p>
 * The Application YAML (e.g. <tt>bip-<i>your-app-name</i>.yml</tt>) can
 * override property values by adding them to the {@code bip.framework:redis:cache}
 * section:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis:cache}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>defaultExpires</td><td>86400</td><td>Long</td></tr>
 * <tr><td>expires</td><td>null</td><td>List&lt;RedisExpires&gt;</td></tr>
 * </table>
 * <p>
 * The {@link RedisExpires} list is populated from list entries in the application yaml
 * under {@code bip.framework:redis:cache:expires}.
 *
 */
@ConfigurationProperties(prefix = "bip.framework.cache")
@Configuration
public class BipRedisCacheProperties {

	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRedisCacheProperties.class);

	/** List of inner class {@link RedisExpires} configuration objects */
	private List<RedisExpires> expires;

	/** The default expiration time */
	private Long defaultExpires = 86400L;

	/**
	 * The inner class {@link RedisExpires} configuration object.
	 *
	 * @param expires
	 */
	public void setExpires(final List<RedisExpires> expires) {
		this.expires = expires;
	}

	/**
	 * Default expiration time if cache does not appear in the expires list.
	 *
	 * @param defaultExpires the default expiration time
	 */
	public void setDefaultExpires(final Long defaultExpires) {
		this.defaultExpires = defaultExpires;
	}

	/**
	 * List of inner class {@link RedisExpires} configuration objects.
	 *
	 * @return List of RedisExpires objects
	 */
	public List<RedisExpires> getExpires() {
		return this.expires;
	}

	/**
	 * Default expiration time if cache does not appear in the expires list.
	 *
	 * @return Long the default expiration time
	 */
	public Long getDefaultExpires() {
		return this.defaultExpires;
	}

	/**
	 * Inner class to hold the time to live (ttl) for a given cache name.
	 * <p>
	 * A list of RedisExpires objects is populated from list entries in the application yaml
	 * under {@code bip.framework:redis:cache:expires}.
	 *
	 */
	public static class RedisExpires {

		/** The cache name */
		private String name;

		/** The time-to-live for items cached under the cache name */
		private Long ttl;

		/**
		 * Redis cache name for which to set the time-to-live.
		 *
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * Redis cache name for which to set the time-to-live.
		 *
		 * @param name
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * Time-to-live for items cached under the cache name.
		 *
		 * @return Long
		 */
		public Long getTtl() {
			return ttl;
		}

		/**
		 * Time-to-live for items cached under the cache name.
		 *
		 * @param ttl
		 */
		public void setTtl(final Long ttl) {
			this.ttl = ttl;
		}
	}

}
