package gov.va.bip.framework.sqs.config;

import gov.va.bip.framework.aws.config.ConfigConstants;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
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
public class SqsProperties {

	private BipLogger logger = BipLoggerFactory.getLogger(SqsProperties.class);

	@Value("false")
	private Boolean enabled;

	@Value("us-east-1")
	private String region;

	//FifoQueue - Whether the queue(s) should be Fifo (setting used for both DLQ and Queue - they must match)
	//true = Exactly-Once Processing (FIFO queue), false = At-Least-Once
	@Value("false")
	private Boolean queuetype;

	//Queue Endpoint
	private String endpoint;

	//ContentBasedDeduplication
	@Value("false")
	private Boolean contentbasedduplication;

	//DelaySeconds
	@Min(0)
	@Max(900) // 15 minutes
	@Value("0")
	private Integer delay;

	//MaximumMessageSize - in bytes
	@Min(1024) // 1 KiB
	@Max(262144) // 256 KiB
	@Value("262144") // 256 KiB
	private String maxmessagesize;

	//MessageRetentionPeriod - in seconds
	@Min(0)
	@Max(1209600) // 14 days
	@Value("345600") // 4 days
	private String messageretentionperiod;

	//ReceiveMessageWaitTimeSeconds
	@Min(0)
	@Max(20)
	@Value("0")
	private Integer waittime;

	//VisibilityTimeout
	@Min(0)
	@Max(43200) // 12 hours
	@Value("30")
	private Integer visibilitytimeout;

	@Value("false")
	private Boolean dlqenabled;

	//RedrivePolicy - DLQ Endpoint
	private String dlqendpoint;

	//RedrivePolicy - Max Receive Count
	@Value("1")
	private String maxreceivecount;

	//Dead Letter Queue - ContentBasedDeduplication
	@Value("false")
	private Boolean dlqcontentbasedduplication;

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

	private String accessKey = ConfigConstants.AWS_LOCALSTACK_ID;
	private String secretKey = ConfigConstants.AWS_LOCALSTACK_KEY;

	//For SQS Configuration
	@Min(0)
	private Integer numberofmessagestoprefetch;

	//For Possible Message Listener Setup in Application Code
	//Defines the maximum number of times the message can enter the DLQ
	@Min(0)
	@Value("0")
	private Integer retries;

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

	public Boolean getQueuetype() {
		return queuetype;
	}

	public void setQueuetype(Boolean queuetype) {
		this.queuetype = queuetype;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Boolean getContentbasedduplication() {
		return contentbasedduplication;
	}

	public void setContentbasedduplication(Boolean contentbasedduplication) {
		this.contentbasedduplication = contentbasedduplication;
	}

	public String getMaxreceivecount() {
		return maxreceivecount;
	}

	public void setMaxreceivecount(String maxreceivecount) {
		this.maxreceivecount = maxreceivecount;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public String getMaxmessagesize() {
		return maxmessagesize;
	}

	public void setMaxmessagesize(String maxmessagesize) {
		this.maxmessagesize = maxmessagesize;
	}

	public String getMessageretentionperiod() {
		return messageretentionperiod;
	}

	public void setMessageretentionperiod(String messageretentionperiod) {
		this.messageretentionperiod = messageretentionperiod;
	}

	public Integer getWaittime() {
		return waittime;
	}

	public void setWaittime(Integer waittime) {
		this.waittime = waittime;
	}

	public Integer getVisibilitytimeout() {
		return visibilitytimeout;
	}

	public void setVisibilitytimeout(Integer visibilitytimeout) {
		this.visibilitytimeout = visibilitytimeout;
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

	public Boolean getDlqcontentbasedduplication() {
		return dlqcontentbasedduplication;
	}

	public void setDlqcontentbasedduplication(Boolean dlqcontentbasedduplication) {
		this.dlqcontentbasedduplication = dlqcontentbasedduplication;
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
		if (dlqdelay == null) {
			return getDelay();
		}
		return dlqwaittime;
	}

	public void setDlqwaittime(Integer dlqwaittime) {
		this.dlqwaittime = dlqwaittime;
	}

	public Integer getDlqvisibilitytimeout() {
		if (dlqdelay == null) {
			return getDelay();
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

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public String getQueueName() {
		return parseQueueName(endpoint);
	}

	public String getDLQQueueName() {
		return parseQueueName(dlqendpoint);
	}

	private String parseQueueName(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		String path = endpointUri.getPath();
		int pos = path.lastIndexOf('/');
		logger.info("path: {}", path);
		return path.substring(pos + 1);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		logger.info("accessKey: {}", accessKey);
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		logger.info("secretKey: {}", secretKey);
		this.secretKey = secretKey;
	}

}
