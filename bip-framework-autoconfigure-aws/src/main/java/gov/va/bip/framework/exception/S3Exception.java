package gov.va.bip.framework.exception;


import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageSeverity;
import org.springframework.http.HttpStatus;

public class S3Exception extends BipRuntimeException {

        public S3Exception(MessageKey key, MessageSeverity severity, HttpStatus status, String... params) {
            super(key, severity, status, params);
        }

        public S3Exception(MessageKey key, MessageSeverity severity, HttpStatus status, Throwable cause, String... params) {
            super(key, severity, status, cause, params);
        }
}