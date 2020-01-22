package gov.va.bip.framework.localstack.autoconfigure;

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
        LocalstackSnsProperties.class
})
@ConfigurationProperties(prefix = "bip.framework.localstack.services")
@ConditionalOnProperty(value = "bip.framework.localstack.enabled")
public class LocalstackProperties {

    @Autowired
    private LocalstackSnsProperties snsProperties;

    @Autowired
    private LocalstackSqsProperties sqsProperties;

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

            if (snsProperties.isEnabled()) {
                this.services.add(new Services("sns", snsProperties.getPort()));
            }

            if (sqsProperties.isEnabled()) {
                this.services.add(new Services("sqs", sqsProperties.getPort()));
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
}
