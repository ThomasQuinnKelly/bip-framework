package gov.va.bip.framework.localstack.autoconfigure;

import gov.va.bip.framework.localstack.s3.config.LocalstackS3Properties;
import gov.va.bip.framework.localstack.sns.config.LocalstackSnsProperties;
import gov.va.bip.framework.localstack.sqs.config.LocalstackSqsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EnableConfigurationProperties({
        LocalstackSqsProperties.class,
        LocalstackSnsProperties.class,
        LocalstackS3Properties.class
})
@ConfigurationProperties(prefix = "bip.framework.localstack.services")
@ConditionalOnProperty(value = "bip.framework.localstack.enabled")
public class LocalstackProperties {

    @Autowired
    private LocalstackSnsProperties localstackSnsProperties;

    @Autowired
    private LocalstackSqsProperties localstackSqsProperties;

    @Autowired
    private LocalstackS3Properties localstackS3Properties;

    /**
     * Create enabled service definitions used on Localstack.
     */
    private List<Services> services;

    public void setServices(List<Services> services) {
        this.services = services;
    }

    public List<Services> getServices() {
        if (this.services == null) {

            this.services = new ArrayList<>();

            if (localstackSnsProperties.isEnabled()) {
                this.services.add(new Services("sns", localstackSnsProperties.getPort()));
            }

            if (localstackSqsProperties.isEnabled()) {
                this.services.add(new Services("sqs", localstackSqsProperties.getPort()));
            }

            if (localstackS3Properties.isEnabled()) {
                this.services.add(new Services("s3", localstackS3Properties.getPort()));
            }

            // ... more services to come
        }
        return this.services;
    }

    /** Inner class with Services specific config properties */
    public class Services {

        /** AWS Service name */
        private String name;

        /** AWS Service port */
        private int port;

        /**
         * Instantiate an object that defines Localstack service properties.
         *
         * @param name
         * @param port
         */
        public Services(String name, int port) {
            this.name = name;
            this.port = port;
        }

        public Services() {
            super();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public LocalstackSnsProperties getLocalstackSnsProperties() {
        return localstackSnsProperties;
    }

    public void setLocalstackSnsProperties(LocalstackSnsProperties localstackSnsProperties) {
        this.localstackSnsProperties = localstackSnsProperties;
    }

    public LocalstackSqsProperties getLocalstackSqsProperties() {
        return localstackSqsProperties;
    }

    public void setLocalstackSqsProperties(LocalstackSqsProperties localstackSqsProperties) {
        this.localstackSqsProperties = localstackSqsProperties;
    }

    public LocalstackS3Properties getLocalstackS3Properties() {
        return localstackS3Properties;
    }

    public void setLocalstackS3Properties(LocalstackS3Properties localstackS3Properties) {
        this.localstackS3Properties = localstackS3Properties;
    }
}
