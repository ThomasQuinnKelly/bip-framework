package gov.va.bip.framework.rest.client.resttemplate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import gov.va.bip.framework.rest.client.resttemplate.RestClientTemplate;

@Configuration
public class TestRestAutoConfiguration {

	@Bean("restClientTemplate")
	@ConditionalOnMissingBean
	public RestClientTemplate restClientTemplate() {
		return new RestClientTemplate();
	}

	@Bean("restClientTemplateWithParam")
	public RestClientTemplate restClientTemplateWithParam() {
		return new RestClientTemplate(new RestTemplate());
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
}