package gov.va.bip.framework.s3.config;

import gov.va.bip.framework.config.AwsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;

/**
 * See:
 * Topics: https://docs.aws.amazon.com/s3/latest/api/API_SetTopicAttributes.html
 * Subscriptions: https://docs.aws.amazon.com/s3/latest/api/API_SetSubscriptionAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.s3", ignoreUnknownFields = false)
public class S3Properties extends AwsProperties {

	private Logger logger = LoggerFactory.getLogger(S3Properties.class);

	@Value("false")
	private Boolean enabled;

	@Value("us-east-1")
	private String region;

	//Queue Endpoint
	private String endpoint;

	private List<Bucket> buckets;

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

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> bucketList) {
		this.buckets = bucketList;
	}

	public static class Bucket {

		private String name;

		public Bucket() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public String getS3BaseUrl() {
		return parseBaseUrl(endpoint);
	}

	private String parseBaseUrl(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		return "http://"+endpointUri.getHost()+":"+endpointUri.getPort();
	}
}
