package gov.va.ocp.framework.security.jwt;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Class used as authentication properties in projects.
 * The values assigned to members in this class are defaults,
 * and are typically overridden in yml and spring configuration.
 */
@ConfigurationProperties(prefix = "ocp.security.jwt")
public class JwtAuthenticationProperties {
	private boolean enabled = true;
	private String header = "Authorization";
	private String secret = "secret";
	private String issuer = "Vets.gov";
	private int expireInSeconds = 900;
	private String[] filterProcessUrls = { "/api/**" };
	private String[] excludeUrls = { "/**" };

	public static final int AUTH_ORDER = SecurityProperties.BASIC_AUTH_ORDER - 2;
	public static final int NO_AUTH_ORDER = AUTH_ORDER + 1;

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
	 * Authentication header
	 *
	 * @return String
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Authentication header
	 *
	 * @param header
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Authentication secret
	 *
	 * @return String
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Authentication issuer
	 *
	 * @return String
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * Authentication secret
	 *
	 * @param secret
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * Authentication issuer
	 *
	 * @param issuer
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * A wildcard URL that filters with URls to process
	 *
	 * @return String
	 */
	public String[] getFilterProcessUrls() {
		return filterProcessUrls;
	}

	/**
	 * A wildcard URL / path that filters which URls to process
	 *
	 * @param filterProcessUrl
	 */
	public void setFilterProcessUrls(String[] filterProcessUrls) {
		this.filterProcessUrls = filterProcessUrls;
	}

	/**
	 * An array of wildcard URLs / paths should be excluded from processing
	 *
	 * @return String[]
	 */
	public String[] getExcludeUrls() {
		return excludeUrls;
	}

	/**
	 * An array of wildcard URLs / paths should be excluded from processing
	 *
	 * @param excludeUrls
	 */
	public void setExcludeUrls(String[] excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	/**
	 * Request expiration time expressed in seconds
	 *
	 * @return int
	 */
	public int getExpireInSeconds() {
		return expireInSeconds;
	}

	/**
	 * Request expiration time expressed in seconds
	 *
	 * @param expireInSeconds
	 */
	public void setExpireInSeconds(int expireInSeconds) {
		this.expireInSeconds = expireInSeconds;
	}

}
