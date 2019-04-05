package gov.va.bip.framework.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

import gov.va.bip.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProvider;
import gov.va.bip.framework.security.jwt.JwtParser;

@Configuration
@ComponentScan(basePackages = { "gov.va.bip.framework.security", "gov.va.bip.framework.security.jwt" })
public class BipSecurityTestConfig {
	@Bean
	JwtAuthenticationProperties jwtAuthenticationProperties() {
		return new JwtAuthenticationProperties();
	}

	@Bean
	protected AuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider(new JwtParser(jwtAuthenticationProperties()));
	}
}
