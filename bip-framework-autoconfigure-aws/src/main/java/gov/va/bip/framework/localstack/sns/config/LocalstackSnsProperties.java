package gov.va.bip.framework.localstack.sns.config;

import gov.va.bip.framework.localstack.autoconfigure.LocalstackServiceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "bip.framework.localstack.services.sns")
@ConditionalOnProperty(value = "bip.framework.localstack.enabled")
public class LocalstackSnsProperties implements LocalstackServiceProperties {

    @Value("false")
    boolean enabled;

    @Min(1025)
    @Max(65536)
    @Value("4575")
    int port;

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
