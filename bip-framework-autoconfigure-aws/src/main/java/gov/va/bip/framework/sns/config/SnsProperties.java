package gov.va.bip.framework.sns.config;

import gov.va.bip.framework.config.AwsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/sns/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/sns/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.sns", ignoreUnknownFields = false)
public class SnsProperties extends AwsProperties {

	private Logger logger = LoggerFactory.getLogger(SnsProperties.class);

	private String name;
	private String type;
	private String topic;
	private String message;

	private String topicArn;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger (Logger logger) {
		this.logger = logger;
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

	public String getTopicArn() {
		return topicArn;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}

}
