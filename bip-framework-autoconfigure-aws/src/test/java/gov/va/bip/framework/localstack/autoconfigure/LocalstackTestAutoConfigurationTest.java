package gov.va.bip.framework.localstack.autoconfigure;

import gov.va.bip.framework.sqs.config.SqsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LocalstackTestAutoConfigurationTest {

	@Bean
	@ConditionalOnMissingBean
	public SqsProperties sqsProperties() {
		SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("https://localhost/180197991925/evssstandardqueue");
		sqsProperties.setDlqendpoint("https://localhost/180197991925/evssdeadletterqueue");
		return sqsProperties;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public LocalstackAutoConfiguration localstackAutoConfiguration() {

		LocalstackProperties localstackProperties = new LocalstackProperties();
		LocalstackProperties.Services service;
		List<LocalstackProperties.Services> services = new ArrayList<>();

		service = new LocalstackProperties().new Services();
		service.setName("sns");
		service.setPort(4575);
		services.add(service);

		service = new LocalstackProperties().new Services();
		service.setName("sqs");
		service.setPort(4576);
		services.add(service);
		localstackProperties.setServices(services);
		
		return  localstackAutoConfiguration();
	}
	
}