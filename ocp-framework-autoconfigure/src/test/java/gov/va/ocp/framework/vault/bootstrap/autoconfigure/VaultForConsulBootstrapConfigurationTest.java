package gov.va.ocp.framework.vault.bootstrap.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.vault.config.VaultBootstrapConfiguration;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.cloud.vault.config.consul.VaultConsulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

import gov.va.ocp.framework.vault.bootstrap.autoconfigure.VaultForConsulBootstrapConfiguration;

public class VaultForConsulBootstrapConfigurationTest {
	
	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations
					.of(VaultForConsulBootstrapConfiguration.class));

	@Test
	public void shouldConfigureConsulToken() {

		this.contextRunner.withUserConfiguration(MockConfiguration.class)
				.withPropertyValues("spring.cloud.vault.consul.enabled=true",
						"spring.cloud.vault.consul.role=test")
				.run(context -> {
					assertThat(context.getEnvironment().getProperty("spring.cloud.consul.discovery.acl-token"))
						.isEqualTo("test");
					
				});
	}
	
	

	@EnableConfigurationProperties({VaultProperties.class, VaultConsulProperties.class})
	private static class MockConfiguration {

		
		@Bean
		VaultOperations vaultOperations() {
			VaultResponse response = new VaultResponse();
			response.setData(Collections.singletonMap("token", "test"));
			
			VaultOperations mock = mock(VaultOperations.class);
			when(mock.read("consul/creds/test")).thenReturn(response);
			return mock;
		}

		@Bean
		VaultBootstrapConfiguration.TaskSchedulerWrapper taskSchedulerWrapper() {
			return new VaultBootstrapConfiguration.TaskSchedulerWrapper(
					mock(ThreadPoolTaskScheduler.class));
		}

	}
	
}
