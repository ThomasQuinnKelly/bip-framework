package gov.va.bip.framework.security.autoconfigure;

import gov.va.bip.framework.client.rest.template.RestClientTemplate;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.rest.exception.BasicErrorController;
import gov.va.bip.framework.security.handler.JwtAuthenticationEntryPoint;
import gov.va.bip.framework.security.handler.JwtAuthenticationSuccessHandler;
import gov.va.bip.framework.security.jwt.*;
import gov.va.bip.framework.security.opa.BipOpaProperties;
import gov.va.bip.framework.security.opa.voter.BipOpaVoter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;
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
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoConfiguration for various authentication types on the Platform (basic
 * authentication, JWT)
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties({ JwtAuthenticationProperties.class, BipOpaProperties.class })
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

		@Autowired
		private RestClientTemplate restClientTemplate;

		/**
		 * Configure.
		 *
		 * @param http the HttpSecurity
		 * @throws Exception the exception
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http
					.authorizeRequests();

			boolean isOpaEnabled = false;

			if (opaProperties.isEnabled()) {
				if (opaProperties.getUrls() != null && opaProperties.getUrls().length > 0
						&& !opaProperties.getUrls()[0].isEmpty()) {
					LOGGER.info(
							"Setting AccessDecisionManager to use Open Policy Agent (OPA) with BipOpaVoter (AccessDecisionVoter)");
					urlRegistry.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated()
							.accessDecisionManager(setAccessDecisionManager());
					isOpaEnabled = true;
				} else {
					LOGGER.warn(BipBanner.newBanner("Open Policy Agent Missing Configuration", Level.WARN),
							"Property to enable OPA set to true, however Urls property is missing");
				}
			}

			if (!isOpaEnabled) {
				urlRegistry.antMatchers(jwtAuthenticationProperties.getFilterProcessUrls()).authenticated();
			}

			urlRegistry.and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();

			http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();

			http.headers()
					.addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy","script-src 'self'"));

		}

		/**
		 * Access decision manager. <br/>
		 * <br/>
		 * 
		 * If the return type is UnanimousBased then it's simple concrete implementation
		 * of {@link org.springframework.security.access.AccessDecisionManager} that
		 * requires all voters to abstain or grant access. <br/>
		 * <br/>
		 * 
		 * If the return type is AffirmativeBased then it's a simple concrete
		 * implementation of
		 * {@link org.springframework.security.access.AccessDecisionManager} that grants
		 * access if any <code>AccessDecisionVoter</code> returns an affirmative
		 * response.
		 *
		 *
		 * @return the access decision manager
		 */
		private AccessDecisionManager setAccessDecisionManager() {
			final String[] opaUrls = opaProperties.getUrls();
			List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
			for (String opaUrl : opaUrls) {
				if (StringUtils.isNotBlank(opaUrl)) {
					LOGGER.info("OPA Url {}", opaUrl);
					decisionVoters.add(new BipOpaVoter(opaUrl, restClientTemplate));
				}
			}
			if (opaProperties.isAllVotersAbstainGrantAccess()) {
				return new UnanimousBased(decisionVoters);
			} else {
				return new AffirmativeBased(decisionVoters);
			}
		}

		/**
		 * Authentication entry point.
		 *
		 * @return the authentication entry point
		 */
		@Bean
		protected AuthenticationEntryPoint authenticationEntryPoint() {
			return new JwtAuthenticationEntryPoint();
		}

		/**
		 * Jwt authentication provider.
		 *
		 * @return the authentication provider
		 */
		@Bean
		protected AuthenticationProvider jwtAuthenticationProvider() {
			return new JwtAuthenticationProvider(new JwtParser(jwtAuthenticationProperties));
		}

		/**
		 * Jwt authentication success handler.
		 *
		 * @return the authentication success handler
		 */
		@Bean
		protected AuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
			return new JwtAuthenticationSuccessHandler();
		}

		/**
		 * Jwt authentication filter.
		 *
		 * @return the jwt authentication filter
		 */
		@Bean
		protected JwtAuthenticationFilter jwtAuthenticationFilter() {
			return new JwtAuthenticationFilter(jwtAuthenticationProperties, jwtAuthenticationSuccessHandler(),
					jwtAuthenticationProvider(), authenticationEntryPoint());
		}

		/**
		 * Open Policy Agent properties used for policy engine authorization.
		 *
		 * @return BipOpaProperties the properties
		 */
		@Bean
		@ConditionalOnMissingBean
		protected BipOpaProperties opaProperties() {
			return new BipOpaProperties();
		}

		/**
		 * The Rest Client Template
		 *
		 * @return RestClientTemplate the rest client template
		 */
		@Bean
		@ConditionalOnMissingBean
		protected RestClientTemplate restClientTemplate() {
			return new RestClientTemplate();
		}
	}

	/**
	 * Adapter that only processes URLs specified in the filter
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "bip.framework.security.jwt", name = "enabled", havingValue = "false")
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtNoWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private JwtAuthenticationProperties jwtAuthenticationProperties;

		@Override
		public void configure(WebSecurity web) {
			web.ignoring().antMatchers(jwtAuthenticationProperties.getFilterProcessUrls());
		}
	}

	/**
	 * Adapter that only excludes specified URLs
	 */
	@Configuration
	@Order(JwtAuthenticationProperties.NO_AUTH_ORDER)
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