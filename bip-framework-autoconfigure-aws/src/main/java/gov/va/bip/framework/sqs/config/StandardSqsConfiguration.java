package gov.va.bip.framework.sqs.config;


import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.context.annotation.*;

@Configuration
@Import({SqsProperties.class})
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

	@Bean
	@Override
	public SQSConnectionFactory connectionFactory(SqsProperties sqsProperties) {
		return createStandardSQSConnectionFactory(sqsProperties);
	}

}
