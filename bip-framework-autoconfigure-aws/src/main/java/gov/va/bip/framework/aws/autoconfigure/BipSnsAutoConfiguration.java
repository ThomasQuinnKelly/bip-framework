package gov.va.bip.framework.aws.autoconfigure;

import gov.va.bip.framework.sns.config.AbstractSnsConfiguration;
import gov.va.bip.framework.sns.config.SnsProperties;
import gov.va.bip.framework.sns.config.StandardSnsConfiguration;
import gov.va.bip.framework.sns.services.SnsService;
import gov.va.bip.framework.sns.services.impl.SnsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(SnsProperties.class)
@ConditionalOnProperty(name = "bip.framework.localstack.services.sns.enabled", havingValue = "true")
@Import({ AbstractSnsConfiguration.class, StandardSnsConfiguration.class })
public class BipSnsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SnsService snsService() {
        return new SnsServiceImpl();
    }

}
