package gov.va.bip.framework.aws.autoconfigure;

import gov.va.bip.framework.s3.config.AbstractS3Configuration;
import gov.va.bip.framework.s3.config.S3Properties;
import gov.va.bip.framework.s3.config.StandardS3Configuration;
import gov.va.bip.framework.s3.services.S3Service;
import gov.va.bip.framework.s3.services.impl.S3ServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@Import({AbstractS3Configuration.class, StandardS3Configuration.class })
public class BipS3AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public S3Service s3Service() {
        return new S3ServiceImpl();
    }

}
