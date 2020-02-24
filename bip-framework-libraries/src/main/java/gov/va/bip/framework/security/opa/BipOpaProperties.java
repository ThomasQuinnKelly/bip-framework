package gov.va.bip.framework.security.opa;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Class used as open policy agent properties in projects. The values assigned
 * to members in this class are defaults, and are typically overridden in YAML
 * and spring configuration.
 */
@ConfigurationProperties(prefix = "bip.framework.security.opa")
public class BipOpaProperties {

	/** The enabled. */
	private boolean enabled = false;

	/** The all voters abstain grant access. */
	private boolean allVotersAbstainGrantAccess = false;

	/** Open Policy Agent URL. */
	private String[] urls = {};

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
	 * Checks if is all voters abstain grant access.
	 *
	 * @return true, if is all voters abstain grant access
	 */
	public boolean isAllVotersAbstainGrantAccess() {
		return allVotersAbstainGrantAccess;
	}

	/**
	 * Sets the all voters abstain grant access.
	 *
	 * @param allVotersAbstainGrantAccess the new all voters abstain grant access
	 */
	public void setAllVotersAbstainGrantAccess(boolean allVotersAbstainGrantAccess) {
		this.allVotersAbstainGrantAccess = allVotersAbstainGrantAccess;
	}

	/**
	 * @return the urls
	 */
	public String[] getUrls() {
		return urls;
	}

	/**
	 * @param urls the OPA URLs to set
	 */
	public void setUrls(String[] urls) {
		this.urls = urls;
	}
}
