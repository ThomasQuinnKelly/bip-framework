package gov.va.bip.framework.exception;


import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;
import org.springframework.http.HttpStatus;

public class SnsException extends BipRuntimeException {

        public SnsException(MessageKey key, MessageSeverity severity, HttpStatus status, String... params) {
            super(key, severity, status, params);
        }

        public SnsException(MessageKey key, MessageSeverity severity, HttpStatus status, Throwable cause, String... params) {
            super(key, severity, status, cause, params);
        }

    }