package gov.va.bip.framework.sns.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * This class exists to parse out a SNS message sent to SQS
 */
public class SNSMessage {
    @JsonProperty("MessageId")
    String messageId;

    @JsonProperty("Type")
    String type;

    @JsonProperty("Timestamp")
    String timestamp;

    @JsonProperty("Message")
    String message;

    @JsonProperty("TopicArn")
    String topicArn;

    public SNSMessage(String messageId, String type, String timestamp, String message, String topicArn) {
        this.messageId = messageId;
        this.type = type;
        this.timestamp = timestamp;
        this.message = message;
        this.topicArn = topicArn;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
