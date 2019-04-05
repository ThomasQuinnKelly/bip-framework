package gov.va.bip.framework.service.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.aspect.AuditAnnotationAspect;
import gov.va.bip.framework.service.aspect.ServiceTimerAspect;
import gov.va.bip.framework.service.aspect.ServiceValidationAspect;

/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class BipServiceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AuditAnnotationAspect auditAnnotationAspect() {
		return new AuditAnnotationAspect();
	}

	@Bean
	@ConditionalOnMissingBean
	public ServiceTimerAspect serviceTimerAspect() {
		return new ServiceTimerAspect();
	}

	@Bean
	@ConditionalOnMissingBean
	public ServiceValidationAspect serviceValidationAspect() {
		return new ServiceValidationAspect();
	}

}
