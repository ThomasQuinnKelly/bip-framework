package gov.va.bip.framework.sqs.exception;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;
import org.springframework.http.HttpStatus;

public class SqsException extends BipRuntimeException {
    public SqsException(MessageKey key, MessageSeverity severity, HttpStatus status, String... params) {
        super(key, severity, status, params);
    }

    public SqsException(MessageKey key, MessageSeverity severity, HttpStatus status, Throwable cause, String... params) {
        super(key, severity, status, cause, params);
    }

}
