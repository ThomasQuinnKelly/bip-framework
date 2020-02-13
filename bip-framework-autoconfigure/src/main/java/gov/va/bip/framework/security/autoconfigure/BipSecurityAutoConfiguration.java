package gov.va.bip.framework.security.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.rest.exception.BasicErrorController;
import gov.va.bip.framework.security.handler.JwtAuthenticationEntryPoint;
import gov.va.bip.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.bip.framework.security.jwt.JwtAuthenticationFilter;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProperties;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProvider;
import gov.va.bip.framework.security.jwt.JwtParser;
import gov.va.bip.framework.security.jwt.JwtTokenService;
import gov.va.bip.framework.security.jwt.TokenResource;
import gov.va.bip.framework.security.opa.BipOpaProperties;
import gov.va.bip.framework.security.opa.voter.BipOpaVoter;

/**
 * AutoConfiguration for various authentication types on the Platform (basic
 * authentication, JWT)
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class BipSecurityAutoConfiguration {
	
	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipSecurityAutoConfiguration.class);

	/**
	 * Adapter for JWT
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "bip.framework.security.jwt", name = "enabled", matchIfMissing = true)
	@Order(1)
	protected static class JwtWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Autowired
		private BipOpaProperties opaProperties;

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http
					.authorizeRequests();

			if (opaProperties.isEnabled() && opaProperties.getUrls() != null && opaProperties.getUrls().length > 0) {
				LOGGER.info("Open Policy Agent Enabled");
				urlRegistry.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated()
						.accessDecisionManager(accessDecisionManager());
			} else {
				urlRegistry.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated();
			}
			urlRegistry.and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();

			http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();
		}

		private AccessDecisionManager accessDecisionManager() {
			final String[] opaUrls = opaProperties.getUrls();
			List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
			for (String opaUrl : opaUrls) {
				if (StringUtils.isNotBlank(opaUrl)) {
					LOGGER.info("OPA Url {}", opaUrl);
					decisionVoters.add(new BipOpaVoter(opaUrl));
				}
			}
			if (opaProperties.isAllVotersAbstainGrantAccess()) {
				return new UnanimousBased(decisionVoters);
			} else {
				return new AffirmativeBased(decisionVoters);
			}
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
					jwtAuthenticationProvider(), authenticationEntryPoint());
		}
	}

	/**
	 * Adapter that only excludes specified URLs
	 */
	@Configuration
	@Order(2147483641)
	protected static class NoWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getExcludeUrls());
		}

	}

	/**
	 * Security properties used for both JWT and Basic Auth authentication. Spring
	 * configuration (yml / properties, etc) provides values to this object.
	 *
	 * @return JwtAuthenticationProperties the properties
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtAuthenticationProperties jwtAuthenticationProperties() {
		return new JwtAuthenticationProperties();
	}

	/**
	 * Open Policy Agent properties used for policy engine authorization.
	 *
	 * @return BipOpaProperties the properties
	 */
	@Bean
	@ConditionalOnMissingBean
	public BipOpaProperties opaProperties() {
		return new BipOpaProperties();
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
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public BasicErrorController basicErrorController() {
		return new BasicErrorController();
	}

	/**
	 * The REST Controller that creates a "valid" JWT token that can be used for
	 * testing.
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