package gov.va.bip.framework.audit.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import gov.va.bip.framework.audit.AuditLogSerializer;

/**
 * Created by rthota on 8/24/17.
 */

@Configuration
@EnableAsync
public class BipAuditAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AuditLogSerializer auditLogSerializer() {
		return new AuditLogSerializer();
	}
}
