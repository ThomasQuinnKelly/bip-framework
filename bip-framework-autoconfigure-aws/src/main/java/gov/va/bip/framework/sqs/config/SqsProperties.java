package gov.va.bip.framework.sqs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.Optional;

/**
 * See: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html
 */
@ConfigurationProperties(prefix = "bip.framework.aws.sqs", ignoreUnknownFields = false)
public class SqsProperties extends SqsQueueProperties {

	@Autowired
	SqsQueueProperties sqsQueueProperties;
	private Logger logger = LoggerFactory.getLogger(SqsProperties.class);

	//ContentBasedDeduplication
	@Value("false")
	private Boolean contentbaseddeduplication;

	@Value("false")
	private Boolean dlqenabled;

	//RedrivePolicy - DLQ Endpoint
	private String dlqendpoint;

	//RedrivePolicy - Max Receive Count
	@Value("1")
	private String maxreceivecount;

	//Dead Letter Queue - ContentBasedDeduplication
	@Value("false")
	private Boolean dlqcontentbaseddeduplication;

	//Dead Letter Queue - DelaySeconds
	@Min(0)
	@Max(900) // 15 minutes
	private Integer dlqdelay;

	//Dead Letter Queue - MaximumMessageSize - in bytes
	@Min(1024) // 1 KiB
	@Max(262144) // 256 KiB
	private String dlqmaxmessagesize;

	//Dead Letter Queue - MessageRetentionPeriod - in seconds
	@Min(0)
	@Max(1209600) // 14 days
	private String dlqmessageretentionperiod;

	//Dead Letter Queue - ReceiveMessageWaitTimeSeconds
	@Min(0)
	@Max(20)
	private Integer dlqwaittime;

	//Dead Letter Queue - VisibilityTimeout
	@Min(0)
	@Max(43200) // 12 hours
	private Integer dlqvisibilitytimeout;

	@Value("test-key")
	private String accessKey;

	@Value("test-secret")
	private String secretKey;

	//For SQS Configuration - Messaging Provider Configuration
	@Min(0)
	private Integer numberofmessagestoprefetch;

	//For Possible Message Listener Setup in Application Code
	//Defines the maximum number of times the message can enter the DLQ
	@Min(0)
	@Value("0")
	private Integer retries;

	public SqsQueueProperties getSqsQueueProperties() {
		return sqsQueueProperties;
	}

	public void setSqsQueueProperties(SqsQueueProperties sqsQueueProperties) {
		this.sqsQueueProperties = sqsQueueProperties;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Boolean getContentbaseddeduplication() {
		return contentbaseddeduplication;
	}

	public void setContentbaseddeduplication(Boolean contentbaseddeduplication) {
		this.contentbaseddeduplication = contentbaseddeduplication;
	}
	
	public String getMaxreceivecount() {
		return maxreceivecount;
	}

	public void setMaxreceivecount(String maxreceivecount) {
		this.maxreceivecount = maxreceivecount;
	}

	public Boolean getDlqenabled() {
		return dlqenabled;
	}

	public void setDlqenabled(Boolean dlqenabled) {
		this.dlqenabled = dlqenabled;
	}

	public String getDlqendpoint() {
		return dlqendpoint;
	}

	public void setDlqendpoint(String dlqendpoint) {
		this.dlqendpoint = dlqendpoint;
	}

	public Boolean getDlqcontentbaseddeduplication() {
		return dlqcontentbaseddeduplication;
	}

	public void setDlqcontentbaseddeduplication(Boolean dlqcontentbaseddeduplication) {
		this.dlqcontentbaseddeduplication = dlqcontentbaseddeduplication;
	}

	public Integer getDlqdelay() {
		if (dlqdelay == null) {
			return getDelay();
		}

		return dlqdelay;
	}

	public void setDlqdelay(Integer dlqdelay) {
		this.dlqdelay = dlqdelay;
	}

	public String getDlqmaxmessagesize() {
		if (dlqmaxmessagesize == null) {
			return getMaxmessagesize();
		}
		return dlqmaxmessagesize;
	}

	public void setDlqmaxmessagesize(String dlqmaxmessagesize) {
		this.dlqmaxmessagesize = dlqmaxmessagesize;
	}

	public String getDlqmessageretentionperiod() {
		if (dlqmessageretentionperiod == null) {
			return getMessageretentionperiod();
		}
		return dlqmessageretentionperiod;
	}

	public void setDlqmessageretentionperiod(String dlqmessageretentionperiod) {
		this.dlqmessageretentionperiod = dlqmessageretentionperiod;
	}

	public Integer getDlqwaittime() {
		if (dlqwaittime == null) {
			return getWaittime();
		}

		return dlqwaittime;
	}

	public void setDlqwaittime(Integer dlqwaittime) {
		this.dlqwaittime = dlqwaittime;
	}

	public Integer getDlqvisibilitytimeout() {
		if (dlqvisibilitytimeout == null) {
			return getVisibilitytimeout();
		}

		return dlqvisibilitytimeout;
	}

	public void setDlqvisibilitytimeout(Integer dlqvisibilitytimeout) {
		this.dlqvisibilitytimeout = dlqvisibilitytimeout;
	}

	public Optional<Integer> getPrefetch() {
		return getNumberofmessagestoprefetch();
	}

	public Optional<Integer> getNumberofmessagestoprefetch() {
		return Optional.ofNullable(numberofmessagestoprefetch);
	}

	public void setNumberofmessagestoprefetch(Integer numberofmessagestoprefetch) {
		this.numberofmessagestoprefetch = numberofmessagestoprefetch;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public String getQueueName() {

		return parseQueueName(getEndpoint());
	}

	public String getDLQQueueName() {
		return parseQueueName(dlqendpoint);
	}

	private String parseQueueName(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		String path = endpointUri.getPath();
		int pos = path.lastIndexOf('/');
		return path.substring(pos + 1);
	}
}
