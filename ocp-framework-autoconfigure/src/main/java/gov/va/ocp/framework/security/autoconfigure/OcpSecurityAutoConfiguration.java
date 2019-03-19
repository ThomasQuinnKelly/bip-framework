package gov.va.ocp.framework.security.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.va.ocp.framework.security.handler.JwtAuthenticationEntryPoint;
import gov.va.ocp.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationFilter;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationProvider;
import gov.va.ocp.framework.security.jwt.JwtParser;
import gov.va.ocp.framework.security.jwt.JwtTokenService;
import gov.va.ocp.framework.security.jwt.TokenResource;

/**
 * Autoconfiguration for various authentication types on the Platform (basic auth, JWT)
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(JwtAuthenticationProperties.class)
public class OcpSecurityAutoConfiguration {

	/**
	 * Adapter for JWT
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "ocp.security.jwt", name = "enabled", matchIfMissing = true)
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {
		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated()
					.and()
					.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
					.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and().csrf().disable();
			http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();

		}

		@Bean
		protected AuthenticationEntryPoint authenticationEntryPoint() {
			return new JwtAuthenticationEntryPoint();
		}

		@Bean
		protected AuthenticationProvider jwtAuthenticationProvider() {
			return new JwtAuthenticationProvider(new JwtParser(jwtAuthenticationProperties));
		}

		@Bean
		protected AuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
			return new JwtAuthenticationSuccessHandler();
		}

		@Bean
		protected JwtAuthenticationFilter jwtAuthenticationFilter() {
			return new JwtAuthenticationFilter(jwtAuthenticationProperties, jwtAuthenticationSuccessHandler(),
					jwtAuthenticationProvider());
		}
	}

	/**
	 * Adapter that only processes URLs specified in the filter
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "ocp.security.jwt", name = "enabled", havingValue = "false")
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtNoWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getFilterProcessUrls());
		}

	}

	/**
	 * Adapter that only excludes specified URLs
	 */
	@Configuration
	@Order(JwtAuthenticationProperties.NO_AUTH_ORDER)
	protected static class NoWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getExcludeUrls());
		}

	}

	/**
	 * Security properties used for both JWT and Basic Auth authentication.
	 * Spring configuration (yml / properties, etc) provides values to this object.
	 *
	 * @return JwtAuthenticationProperties the properties
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtAuthenticationProperties jwtAuthenticationProperties() {
		return new JwtAuthenticationProperties();
	}

	/**
	 * The service component for processing JWT
	 *
	 * @return JwtTokenService the service component
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtTokenService jwtTokenService() {
		return new JwtTokenService();
	}

	/**
	 * The REST Controller that creates a "valid" JWT token that can be used for testing.
	 *
	 * @return TokenResource the rest controller
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnExpression("${ocp.security.jwt.enabled:true} && ${ocp.security.jwt.generate.enabled:true}")
	public TokenResource tokenResource() {
		return new TokenResource();
	}
}