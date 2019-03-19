/**
 *
 */
package gov.va.ocp.framework.util;

import java.util.UUID;

import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.security.SecurityUtils;
import gov.va.ocp.framework.service.DomainResponse;

/**
 * Utils for cache
 *
 * @author akulkarni
 */
public final class OcpCacheUtil {

	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(OcpCacheUtil.class);
	protected static final String SEPARATOR = "_";

	/**
	 * hide constructor.
	 */
	private OcpCacheUtil() {
	}

	public static boolean checkResultConditions(DomainResponse domainResponse) {
		return domainResponse == null || domainResponse.hasErrors() || domainResponse.hasFatals()
				|| domainResponse.isDoNotCacheResponse();
	}

	/**
	 * Generate a unique user based complex cache key. This implementation uses the
	 * user's unique username and the beneficiary as a prefix for the rest of the
	 * complex key
	 *
	 * @param keyValues
	 *            the key values
	 * @return the user based key
	 */
	public static String getUserBasedKey(final Object... keyValues) {
		return getUserBasedKey() + SEPARATOR + createKey(keyValues);
	}

	/**
	 * Generate a unique user based cache key. This implementation uses the user's
	 * unique username and the beneficiary
	 *
	 * @return the user based key
	 */
	public static String getUserBasedKey() {
		if (SecurityUtils.getPersonTraits() != null) {
			String prefix;

			if (SecurityUtils.getPersonTraits().getPid() != null
					&& SecurityUtils.getPersonTraits().getPid().length() > 0) {
				prefix = SecurityUtils.getPersonTraits().getPid();
			} else if (SecurityUtils.getPersonTraits().getFileNumber() != null
					&& SecurityUtils.getPersonTraits().getFileNumber().length() > 0) {
				prefix = SecurityUtils.getPersonTraits().getFileNumber();
			} else {
				prefix = UUID.randomUUID().toString();
			}
			String key = createKey(prefix, SecurityUtils.getPersonTraits().getFirstName(),
					SecurityUtils.getPersonTraits().getLastName());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generating cache key with value of " + key);
			}
			return key;
		} else {
			LOGGER.error("There was no user principal established to base the cache key on."
					+ " Returning a null key value.");
			// Returning null will cause an IllegalArguementException in the cache
			// framework, letting us know
			// there was a cache key generation error
			return null;
		}
	}

	/**
	 * Creates a unique cache key using the given key values.
	 *
	 * @param keyValues
	 *            the key values
	 * @return the string
	 */
	public static final String createKey(final Object... keyValues) {
		final StringBuilder cacheKey = new StringBuilder();
		for (Object key : keyValues) {
			if (key != null) {
				if (cacheKey.length() > 0) {
					cacheKey.append(SEPARATOR);
				}
				cacheKey.append(key.hashCode());
			}
		}
		return cacheKey.toString();
	}

}
