package gov.va.bip.framework.sqs.config;

import gov.va.bip.framework.aws.config.ConfigConstants;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.Optional;

@ConfigurationProperties(prefix = "bip.framework.aws.sqs", ignoreUnknownFields = false)
public class SqsProperties {

	private BipLogger logger = BipLoggerFactory.getLogger(SqsProperties.class);

	@Value("false")
	private Boolean enabled;

	private String region;
	private String endpoint;
	private String dlqendpoint;
	private int retries;
	private Integer prefetch;
	private Boolean queuetype;
	private Boolean contentbasedduplication;
	private Integer delay;
	private String maxmessagesize;
	private String messageretentionperiod;
    private Integer waittime;
    private Integer visibilitytimeout;
    private Integer numberofmessagestoprefetch;
    private Integer dlqretriescount;
	
	private String accessKey = ConfigConstants.AWS_LOCALSTACK_ID;
	private String secretKey = ConfigConstants.AWS_LOCALSTACK_KEY;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the queuetype
	 */
	public Boolean getQueuetype() {
		return queuetype;
	}

	/**
	 * @param queuetype the queuetype to set
	 */
	public void setQueuetype(Boolean queuetype) {
		this.queuetype = queuetype;
	}

	/**
	 * @return the contentbasedduplication
	 */
	public Boolean getContentbasedduplication() {
		return contentbasedduplication;
	}

	/**
	 * @param contentbasedduplication the contentbasedduplication to set
	 */
	public void setContentbasedduplication(Boolean contentbasedduplication) {
		this.contentbasedduplication = contentbasedduplication;
	}

	/**
	 * @return the delay
	 */
	public Integer getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	/**
	 * @return the maxmessagesize
	 */
	public String getMaxmessagesize() {
		return maxmessagesize;
	}

	/**
	 * @param maxmessagesize the maxmessagesize to set
	 */
	public void setMaxmessagesize(String maxmessagesize) {
		this.maxmessagesize = maxmessagesize;
	}

	/**
	 * @return the messageretentionperiod
	 */
	public String getMessageretentionperiod() {
		return messageretentionperiod;
	}

	/**
	 * @param messageretentionperiod the messageretentionperiod to set
	 */
	public void setMessageretentionperiod(String messageretentionperiod) {
		this.messageretentionperiod = messageretentionperiod;
	}

	/**
	 * @return the waittime
	 */
	public Integer getWaittime() {
		return waittime;
	}

	/**
	 * @param waittime the waittime to set
	 */
	public void setWaittime(Integer waittime) {
		this.waittime = waittime;
	}

	/**
	 * @return the visibilitytimeout
	 */
	public Integer getVisibilitytimeout() {
		return visibilitytimeout;
	}

	/**
	 * @param visibilitytimeout the visibilitytimeout to set
	 */
	public void setVisibilitytimeout(Integer visibilitytimeout) {
		this.visibilitytimeout = visibilitytimeout;
	}
	
	public String getQueue() {
		return endpoint;
	}

	public void setQueue(String queue) {
		this.endpoint = queue;
	}

	public String getDeadletterqueue() {
		return dlqendpoint;
	}

	public void setDeadletterqueue(String deadletterqueue) {
		this.dlqendpoint = deadletterqueue;
	}

	public void setPrefetch(Integer prefetch) {
		this.prefetch = prefetch;
	}


	public Optional<Integer> getPrefetch() {
		return Optional.ofNullable(prefetch);
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

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		//logger.info("secretKey: {}", secretKey);
		this.secretKey = secretKey;
	}

	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		//logger.info("accessKey: {}", accessKey);
		this.accessKey = accessKey;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return
	 */
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 *
	 * @return
	 */
	public String getDlqendpoint() {
		return dlqendpoint;
	}

	/**
	 *
	 * @param dlqendpoint
	 */
	public void setDlqendpoint(String dlqendpoint) {
		this.dlqendpoint = dlqendpoint;
	}

	/**
	 *
	 * @return
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 *
	 * @param retries
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	public Integer getDlqretriescount() {
		return dlqretriescount;
	}

	public void setDlqretriescount(Integer dlqretriescount) {
		this.dlqretriescount = dlqretriescount;
	}

	public Integer getNumberofmessagestoprefetch() {
		return numberofmessagestoprefetch;
	}

	public void setNumberofmessagestoprefetch(Integer numberofmessagestoprefetch) {
		this.numberofmessagestoprefetch = numberofmessagestoprefetch;
	}


//scan for all implementing methods of QueueProperties
//private ArrayList<SqsQueueProperties> allQueueProperties;
//	public ArrayList<SqsQueueProperties> getAllQueueProperties() {
//		return allQueueProperties;
//	}
//
//	public void setAllQueueProperties(ArrayList<SqsQueueProperties> allQueueProperties) {
//		this.allQueueProperties = allQueueProperties;
//	}
}
