package gov.va.bip.framework.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

import gov.va.bip.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProvider;
import gov.va.bip.framework.security.jwt.JwtParser;
import gov.va.bip.framework.security.opa.BipOpaProperties;

@Configuration
@ComponentScan(basePackages = { "gov.va.bip.framework.security", "gov.va.bip.framework.security.jwt",
		"gov.va.bip.framework.security.opa" })
public class BipSecurityTestConfig {
	@Bean
	JwtAuthenticationProperties jwtAuthenticationProperties() {
		return new JwtAuthenticationProperties();
	}

	@Bean
	BipOpaProperties bipOpaProperties() {
		return new BipOpaProperties();
	}

	@Bean
	protected AuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider(new JwtParser(jwtAuthenticationProperties()));
	}
}
