package gov.va.bip.framework.s3.config;

import gov.va.bip.framework.config.AwsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/s3/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/s3/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.s3", ignoreUnknownFields = false)
public class S3Properties extends AwsProperties {

	private Logger logger = LoggerFactory.getLogger(S3Properties.class);

	private List<Bucket> buckets;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger (Logger logger) {
		this.logger = logger;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> bucketList) {
		this.buckets = bucketList;
	}

	public static class Bucket {

		private String name;

		public Bucket() {
			// Empty constructor, these get instantiated by config parameters.
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
