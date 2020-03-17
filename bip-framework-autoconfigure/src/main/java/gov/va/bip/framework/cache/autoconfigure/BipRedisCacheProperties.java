package gov.va.bip.framework.cache.autoconfigure;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Properties used to configure the Redis Cache.
 * <p>
 * For each cache used by the application, the Application YAML
 * (e.g. <tt>bip-<i>your-app-name</i>.yml</tt>) must declare
 * properties under {@code bip.framework:cache}:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:cache}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>defaultExpires</td><td>86400</td><td>Long</td></tr>
 * <tr><td>expires</td><td>null</td><td>List&lt;RedisExpires&gt;</td></tr>
 * </table>
 * <p>
 * The {@link RedisExpires} list is populated from list entries in the application yaml
 * under {@code bip.framework:cache:expires}.
 *
 */
@Component
public class BipRedisCacheProperties {
	/** Class logger */
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
