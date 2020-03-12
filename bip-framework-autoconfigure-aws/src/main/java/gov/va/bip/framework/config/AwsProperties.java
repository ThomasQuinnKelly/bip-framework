package gov.va.bip.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/s3/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/s3/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws", ignoreUnknownFields = true)
public class AwsProperties {

	private Logger logger = LoggerFactory.getLogger(AwsProperties.class);

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
