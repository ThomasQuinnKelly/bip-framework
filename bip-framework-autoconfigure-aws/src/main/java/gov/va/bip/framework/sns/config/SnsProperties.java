package gov.va.bip.framework.sns.config;

import gov.va.bip.framework.aws.config.ConfigConstants;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/sns/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/sns/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.sns", ignoreUnknownFields = false)
public class SnsProperties {

	private BipLogger logger = BipLoggerFactory.getLogger(SnsProperties.class);

	@Value("false")
	private Boolean enabled;
	private String name;
	private String type;
	private String topic;
	private String message;
	private String region;
	private String endpoint;
	private int retries;
	private Integer prefetch;
	private String topicArn;
	
	private String accessKey = ConfigConstants.AWS_LOCALSTACK_ID;
	private String secretKey = ConfigConstants.AWS_LOCALSTACK_KEY;

	public BipLogger getLogger() {
		return logger;
	}

	public void setLogger(BipLogger logger) {
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

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public Integer getPrefetch() {
		return prefetch;
	}

	public void setPrefetch(Integer prefetch) {
		this.prefetch = prefetch;
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

}
