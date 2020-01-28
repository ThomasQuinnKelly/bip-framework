package gov.va.bip.framework.sns.dto;

import gov.va.bip.framework.service.DomainResponse;

public class SendMessageResponse extends DomainResponse {

    private static final long serialVersionUID = -2531179599395037607L;

    private String statusCode;

    private String messageId;

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
