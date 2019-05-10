package gov.va.bip.framework.cache.autoconfigure.jmx;

/**
 * A JMX MBean interface definition for caching operations.
 *
 * @author aburkholder
 */
public interface BipCacheOpsMBean {
	/**
	 * Clear all caches in the current spring cache context.
	 */
	public void clearAllCaches();

	/**
	 * Output INFO logs of the current cache configuration property values
	 * in the spring source environment (e.g. from application yaml).
	 */
	public void logCacheConfigProperties();

	/**
	 * Output INFO logs of the JedisConnectionFactory field values
	 * from the current bean in the application context.
	 */
	public void logCurrentJedisConnectionFactoryFields();

	/**
	 * Output INFO logs of the CacheManager field values
	 * from the current bean in the application context.
	 */
	public void logCurrentCacheManagerFields();
}