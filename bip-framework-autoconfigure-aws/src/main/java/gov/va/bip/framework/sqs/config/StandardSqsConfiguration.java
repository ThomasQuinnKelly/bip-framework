package gov.va.bip.framework.sqs.config;

import javax.jms.ConnectionFactory;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

	@Bean
	@Override
	public SQSConnectionFactory connectionFactory(SqsProperties sqsProperties) {
		return createStandardSQSConnectionFactory(sqsProperties);
	}

}
