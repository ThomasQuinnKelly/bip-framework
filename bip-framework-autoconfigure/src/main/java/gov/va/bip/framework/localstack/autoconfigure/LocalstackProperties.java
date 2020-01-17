package gov.va.bip.framework.localstack.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "localstack.services")
@ConditionalOnProperty(value = "localstack.enabled")
public class LocalstackProperties {

    // @Value annotations here are default values unless overridden by localstack.services.snsSetup
    @Value("false")
    boolean snssetup;

    @Value("4575")
    int snsport;

    @Value("false")
    boolean sqssetup;

    @Value("4576")
    int sqsport;

    /**
     * Create enabled service definitions used on Localstack.
     */
    private List<Services> services = new ArrayList<Services>() {
        {
            if (snssetup) {
                add(new Services("sns", snsport));
            }

            if (sqssetup) {
                add(new Services("sqs", sqsport));
            }

            // ... more services to come
        }
    };

    public void setServices(List<Services> services) {
        this.services = services;
    }

    public List<Services> getServices() {
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
            super();
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
