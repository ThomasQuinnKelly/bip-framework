package gov.va.bip.framework.sqs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

	@Bean
	@Override
	public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
		return createStandardSQSConnectionFactory(sqsProperties);
	}

}
