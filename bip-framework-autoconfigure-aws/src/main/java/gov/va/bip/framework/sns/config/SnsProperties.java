package gov.va.bip.framework.sns.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/sns/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/sns/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.sns", ignoreUnknownFields = false)
public class SnsProperties {

	private Logger logger = LoggerFactory.getLogger(SnsProperties.class);

	@Value("false")
	private Boolean enabled;
	private String name;
	private String type;
	private String topic;
	private String message;

	@Value("us-east-1")
	private String region;
	private String endpoint;
	private String topicArn;

	@Value("test-key")
	private String accessKey;

	@Value("test-secret")
	private String secretKey;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger (Logger logger) {
		this.logger = logger;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getTopicArn() {
		return topicArn;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}

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

	public String getSnsBaseUrl() {
		return parseBaseUrl(endpoint);
	}

	private String parseBaseUrl(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		return "http://"+endpointUri.getHost()+":"+endpointUri.getPort();
	}

}
