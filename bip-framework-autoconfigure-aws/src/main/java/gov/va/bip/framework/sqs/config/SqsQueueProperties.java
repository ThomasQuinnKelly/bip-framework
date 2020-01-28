package gov.va.bip.framework.sqs.config;

abstract class SqsQueueProperties {

    String queue;
    String deadletterqueue;
    int retries;
    Integer prefetch;
    Boolean queuetype;
    Boolean contentbasedduplication;
    Integer delay;
    String maxmessagesize;
    String messageretentionperiod;
    Integer waittime;
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

    public Boolean getQueuetype() {
        return queuetype;
    }

    public void setQueuetype(Boolean queuetype) {
        this.queuetype = queuetype;
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
}
