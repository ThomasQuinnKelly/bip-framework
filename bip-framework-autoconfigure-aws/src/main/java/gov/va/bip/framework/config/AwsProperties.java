package gov.va.bip.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "bip.framework.aws", ignoreUnknownFields = true)
public class AwsProperties {

	@Value("test-key")
	private String accessKey;

	@Value("test-secret")
	private String secretKey;

	@Value("false")
	private Boolean enabled;

	@Value("us-east-1")
	private String region;

	//Endpoint
	private String endpoint;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBaseUrl() {
		return parseBaseUrl(endpoint);
	}

	private String parseBaseUrl(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		return "http://"+endpointUri.getHost()+":"+endpointUri.getPort();
	}

}
