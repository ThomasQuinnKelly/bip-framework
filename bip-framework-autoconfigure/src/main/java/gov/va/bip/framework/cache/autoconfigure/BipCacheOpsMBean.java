package gov.va.bip.framework.cache.autoconfigure;

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
}