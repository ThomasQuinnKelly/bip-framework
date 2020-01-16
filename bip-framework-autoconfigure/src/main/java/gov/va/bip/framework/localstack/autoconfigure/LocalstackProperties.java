package gov.va.bip.framework.localstack.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(value = "localstack.enabled")
public class LocalstackProperties {

    @Value("${aws.sns.enabled:false}")
    boolean setupSNS;

    @Value("${aws.sns.port:4575}")
    int snsPort;

    @Value("${aws.sqs.enabled:false}")
    boolean setupSQS;

    @Value("${aws.sqs.port:4576}")
    int sqsPort;

    /**
     * Create enabled service definitions used on Localstack.
     */
    private List<Services> services = new ArrayList<Services>() {
        {
            if (setupSNS) {
                add(new Services("sns", snsPort));
            }

            if (setupSQS) {
                add(new Services("sqs", sqsPort));
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
