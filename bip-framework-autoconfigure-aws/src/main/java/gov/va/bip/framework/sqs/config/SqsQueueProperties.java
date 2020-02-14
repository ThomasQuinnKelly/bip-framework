package gov.va.bip.framework.sqs.config;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

abstract class SqsQueueProperties {

    String queue;
    String deadletterqueue;

    //ContentBasedDeduplication
    @Value("false")
    private Boolean contentbasedduplication;

    //DelaySeconds
    @Min(0)
    @Max(900) // 15 minutes
    @Value("0")
    Integer delay;

    //MaximumMessageSize - in bytes
    @Min(1024) // 1 KiB
    @Max(262144) // 256 KiB
    @Value("262144") // 256 KiB
    String maxmessagesize;

    //MessageRetentionPeriod - in seconds
    @Min(0)
    @Max(1209600) // 14 days
    @Value("345600") // 4 days
    String messageretentionperiod;

    //ReceiveMessageWaitTimeSeconds
    @Min(0)
    @Max(20)
    @Value("0")
    Integer waittime;


    //FifoQueue - Whether the queue(s) should be Fifo (setting used for both DLQ and Queue - they must match)
    //true = Exactly-Once Processing (FIFO queue), false = At-Least-Once
    //Fifo is not supported at this time
    @Value("false")
    private Boolean queuetype;

    //VisibilityTimeout
    @Min(0)
    @Max(43200) // 12 hours
    @Value("30")
    Integer visibilitytimeout;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getDeadletterqueue() {
        return deadletterqueue;
    }

    public void setDeadletterqueue(String deadletterqueue) {
        this.deadletterqueue = deadletterqueue;
    }

    public Boolean getContentbasedduplication() {
        return contentbasedduplication;
    }

    public void setContentbasedduplication(Boolean contentbasedduplication) {
        this.contentbasedduplication = contentbasedduplication;
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

    public Boolean getQueuetype() {
        return queuetype;
    }

    public void setQueuetype(Boolean queuetype) {
        this.queuetype = queuetype;
    }
}
