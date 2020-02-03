package gov.va.bip.framework.security.autoconfigure;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.va.bip.framework.rest.exception.BasicErrorController;
import gov.va.bip.framework.security.handler.JwtAuthenticationEntryPoint;
import gov.va.bip.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.bip.framework.security.jwt.JwtAuthenticationFilter;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProvider;
import gov.va.bip.framework.security.jwt.JwtParser;
import gov.va.bip.framework.security.jwt.JwtTokenService;
import gov.va.bip.framework.security.jwt.TokenResource;
import gov.va.bip.framework.security.opa.voter.OPAVoter;

/**
 * Autoconfiguration for various authentication types on the Platform (basic auth, JWT)
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(JwtAuthenticationProperties.class)
public class BipSecurityAutoConfiguration {

	/**
	 * Adapter for JWT
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "bip.framework.security.jwt", name = "enabled", matchIfMissing = true)
	@Order(1)
	protected static class JwtWebSecurityConfigurerAdapter
	extends WebSecurityConfigurerAdapter {
		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
			.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated().accessDecisionManager(accessDecisionManager())
			.and()
			.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().csrf().disable();
			
			http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();
        }
        
        @Bean
        public AccessDecisionManager accessDecisionManager() {
            List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays
                    .asList(new OPAVoter("http://localhost:8181/v1/data/http/authz/allow"));
            return new UnanimousBased(decisionVoters);
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

		
		protected JwtAuthenticationFilter jwtAuthenticationFilter() {
			return new JwtAuthenticationFilter(jwtAuthenticationProperties, jwtAuthenticationSuccessHandler(),
					jwtAuthenticationProvider(), authenticationEntryPoint());
		}
	}
	
	/**
	 * Adapter that only excludes specified URLs
	 */
	@Configuration
	@Order(2147483641)
	protected static class NoWebSecurityConfigurerAdapter
	extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) {
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


	@Bean
	@ConditionalOnMissingBean
	@Primary
	@Order(Ordered.HIGHEST_PRECEDENCE )
	public BasicErrorController basicErrorController() {
		return new BasicErrorController();
	}
	
	/**
	 * The REST Controller that creates a "valid" JWT token that can be used for testing.
	 *
	 * @return TokenResource the rest controller
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnExpression("${bip.framework.security.jwt.enabled:true} && ${bip.framework.security.jwt.generate.enabled:true}")
	public TokenResource tokenResource() {
		return new TokenResource();
	}
}