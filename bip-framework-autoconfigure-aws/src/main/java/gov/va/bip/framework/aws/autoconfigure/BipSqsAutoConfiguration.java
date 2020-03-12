package gov.va.bip.framework.aws.autoconfigure;

import gov.va.bip.framework.sqs.config.AbstractSqsConfiguration;
import gov.va.bip.framework.sqs.config.SqsProperties;
import gov.va.bip.framework.sqs.config.StandardSqsConfiguration;
import gov.va.bip.framework.sqs.services.SqsService;
import gov.va.bip.framework.sqs.services.impl.SqsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@ConditionalOnProperty(name = "bip.framework.localstack.services.sqs.enabled", havingValue = "true")
@Import({ AbstractSqsConfiguration.class, StandardSqsConfiguration.class })
public class BipSqsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqsService sqsService() {
        return new SqsServiceImpl();
    }

}
