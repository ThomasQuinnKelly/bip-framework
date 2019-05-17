package gov.va.bip.framework.swagger.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by vgadda on 8/3/17.
 */

@ConfigurationProperties(prefix = "bip.framework.swagger")
public class SwaggerProperties {

	private boolean enabled = true;

	/** The secure paths. */
	private String securePaths = "[Api secure paths via bip.framework.swagger.securePaths]";

	/** The group name. */
	private String groupName = "";

	/** The title. */
	private String title = "[Api title via 'bip.framework.swagger.title']";

	/** The description. */
	private String description = "";

	/** The version. */
	private String version = "[Api version via 'bip.framework.swagger.version']";

	/** The contact name. */
	private String contactName = "";

	/** The contact url. */
	private String contactUrl = "";

	/** The contact email. */
	private String contactEmail = "";

	/** The license. */
	private String license = "";

	/** The license url. */
	private String licenseUrl = "";

	/** The terms of service url. */
	private String termsOfServiceUrl = "";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactUrl() {
		return contactUrl;
	}

	public void setContactUrl(String contactUrl) {
		this.contactUrl = contactUrl;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getLicenseUrl() {
		return licenseUrl;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSecurePaths() {
		return securePaths;
	}

	public void setSecurePaths(String securePaths) {
		this.securePaths = securePaths;
	}

	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}
}
