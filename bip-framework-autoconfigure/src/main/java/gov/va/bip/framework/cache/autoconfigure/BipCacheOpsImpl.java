package gov.va.bip.framework.cache.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * A JMX MBean implementation for operations on the current spring cache context.
 *
 * @author aburkholder
 */
@Component
@ManagedResource(objectName = BipCacheOpsImpl.OBJECT_NAME,
		description = "A JMX MBean implementation for operations on the current spring cache context.")
public class BipCacheOpsImpl implements BipCacheOpsMBean {

	/** Domain part of object name for JMX bean */
	private static final String OBJECT_NAME_DOMAIN = "gov.va.bip.cache";
	/** Properties part of object name for JMX bean */
	private static final String OBJECT_NAME_PROPERTIES = "type=Support,name=cacheOps";
	/** The object name for JMX bean */
	static final String OBJECT_NAME = OBJECT_NAME_DOMAIN + ":" + OBJECT_NAME_PROPERTIES;

	/** The configured spring cache manager */
	@Autowired
	private CacheManager cacheManager;

	/**
	 * Instantiate this class.
	 */
	public BipCacheOpsImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.cache.autoconfigure.BipCacheOpsMBean#clearAllCaches()
	 */
	@ManagedOperation(description = "Clear all cache entries known to the spring cache manager.")
	@Override
	public void clearAllCaches() {
		if (cacheManager != null) {
			cacheManager.getCacheNames().parallelStream()
					.forEach(name -> cacheManager.getCache(name).clear());
		}
	}
}
