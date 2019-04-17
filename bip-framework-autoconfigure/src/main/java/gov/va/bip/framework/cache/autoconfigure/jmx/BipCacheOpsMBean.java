package gov.va.bip.framework.cache.autoconfigure.jmx;

/**
 * A JMX MBean interface definition for caching operations.
 *
 * @author aburkholder
 */
@FunctionalInterface // to be removed when more methods are added
public interface BipCacheOpsMBean {
	/**
	 * Clear all caches in the current spring cache context.
	 */
	public void clearAllCaches();
}