package gov.va.bip.framework.localstack.sqs.config;

import gov.va.bip.framework.localstack.autoconfigure.LocalstackServiceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "bip.framework.localstack.services.sqs")
@ConditionalOnProperty(value = "bip.framework.localstack.enabled")
public class LocalstackSqsProperties implements LocalstackServiceProperties {

    @Min(1025)
    @Max(65536)
    @Value("4576")
    int port;

    @Value("false")
    boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}