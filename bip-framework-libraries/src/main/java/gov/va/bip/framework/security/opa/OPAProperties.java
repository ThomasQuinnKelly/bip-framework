package gov.va.bip.framework.security.opa;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Class used as open policy agent properties in projects.
 * The values assigned to members in this class are defaults,
 * and are typically overridden in yml and spring configuration.
 */
@ConfigurationProperties(prefix = "bip.framework.security.opa")
public class OPAProperties {
	
	/** The enabled. */
	private boolean enabled = false;
	
	/** The url. */
	private String[] url = {};

	/**
	 * Authentication enabled
	 *
	 * @return boolean
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Authentication enabled
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the url
	 */
	public String[] getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String[] url) {
		this.url = url;
	}
}
