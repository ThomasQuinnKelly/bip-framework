## What is this project for? ##

BIP Framework Autoconfigure Project is a suite of POM files that provides application services with starter dependencies for the BIP platform. 

## Overview of the packages ##
**gov.va.bip.framework.audit.autoconfigure**: BIP Audit AutoConfiguration that provides bean and enables Async method execution. This project helps in Audit logging the object passed asynchronously.

	@Configuration
	@EnableAsync
	public class BipAuditAutoConfiguration {
	
    		@Bean
    		@ConditionalOnMissingBean
    		public AuditLogSerializer auditLogSerializer() {
       		 return new AuditLogSerializer();
    		}
	}

**gov.va.bip.framework.cache.autoconfigure**: Redis cache auto-configuration. This auto configuration helps in setting up Redis Cache. This class works to set up the Redis Cache server configuration including setting up caches with specific naming methodology, starting the cache server, error handling etc.

	@Configuration
	@EnableConfigurationProperties(BipCacheProperties.class)
	@AutoConfigureAfter(CacheAutoConfiguration.class)
	@EnableCaching
	@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
	public class BipCacheAutoConfiguration extends CachingConfigurerSupport {

**gov.va.bip.framework.feign.autoconfigure**: Feign client auto-configuration is handled in the classes in this package. This includes below:

- Hystrix enablement for Feign client. Define Hystrix properties like Group Key, Threading stratefy etc can be done while creating feignBuilder.
- Define behavior through properties for the Feign client like connectionTimeoOut, etc is possible
- FeignCustomErrorDecoder has been created to interrogate and modify the Exception being propagated. 

		@Configuration
		public class BipFeignAutoConfiguration {

**gov.va.bip.framework.hystrix.autoconfigure**: Hystrix auto-configuration to set RequestAttributes to be passed from ThreadLocal to Hystrix threads through RequestAttributeAwareCallableWrapper bean. This will enable to pass the RequestAttributes from User thread to Hystrix thread(THREAD strategy). 

	@Configuration
	@ConditionalOnProperty(value = "hystrix.wrappers.enabled", matchIfMissing = true)
	public class HystrixContextAutoConfiguration {

**gov.va.bip.framework.modelvalidator.autoconfigure**: This helps in auto-configuration for model validator. LocalValidatorFactoryBean is created and this gives us the ability to further customize the validators behaviour.

	@Configuration
	@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
	public class BipValidatorAutoConfiguration {

**gov.va.bip.framework.rest.autoconfigure**: Auto-configuration for rest template. This autoconfiguration enables RestClientTemplate creation and customization. TokenClientHttpRequestInterceptor passes the JWT token from Request to Response objects being passed through this Interceptor. BipRestGlobalExceptionHandler is configured to handle exceptions from server to client and modify them if needed. ProviderHttpAspect helps in Audit logging of the http request and response and this is more on the Provider than client side execution. RestProviderTimerAspect deals with logging the time taken for the service execution through PerformanceLoggingAspect.

	@Configuration
     public class BipRestAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ProviderHttpAspect providerHttpAspect() {
		return new ProviderHttpAspect();
	}
	@Bean
	@ConditionalOnMissingBean
	public BipRestGlobalExceptionHandler bipRestGlobalExceptionHandler() {
		return new BipRestGlobalExceptionHandler();
	}
	@Bean
	@ConditionalOnMissingBean
	public RestProviderTimerAspect restProviderTimerAspect() {
		return new RestProviderTimerAspect();
	}
	@Bean
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
		…	
	}
	@Bean
	@ConditionalOnMissingBean
	public RestClientTemplate restClientTemplate() {
		…
	}
	@Bean
	@ConditionalOnMissingBean
	public TokenClientHttpRequestInterceptor tokenClientHttpRequestInterceptor() {
		return new TokenClientHttpRequestInterceptor();
	}
    }

**gov.va.bip.framework.security.autoconfigure**: Auto-configuration for security framework using JWT token.
 
- JwtWebSecurityConfigurerAdapter provides configuration for JWT security processing and provides configuration like filters need
to be used to Authenticate, URL's to be processed etc.

	@Configuration
	@ConditionalOnProperty(prefix = "bip.framework.security.jwt", name = "enabled", matchIfMissing = true)
	@Order(JwtAuthenticationProperties.AUTH_ORDER)
	protected static class JwtWebSecurityConfigurerAdapter
			extends WebSecurityConfigurerAdapter {
- JwtWebSecurityConfigurerAdapter defines beans below and thier respective uses:
   a. AuthenticationEntryPoint - Returns error message in case not authenticated
   b. AuthenticationProvider - Bean for decrypting and parsing the JWT
   c. JwtAuthenticationSuccessHandler - Bean for handling successful authentication
   d. JwtAuthenticationFilter - Bean is central to security configuration and helps in configuring springboot starter for the platform security. It configures security using AuthenticationEntryPoint, AuthenticationProvider, JwtAuthenticationSuccessHandler and this class also Audits the JWT Token. 

- TokenResource is used to expose and end point for Token Generation for Swagger page.

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnExpression("${bip.framework.security.jwt.enabled:true} && ${bip.framework.security.jwt.generate.enabled:true}")
		public TokenResource tokenResource() {

**gov.va.bip.framework.service.autoconfigure**: Auto-configuration for service configurations for beans, exceptions and aspect. 

	@Configuration
	public class BipServiceAutoConfiguration {
	
- BipServiceAutoConfiguration has below beans configured and their responsibilities are as below:

  a. ServiceTimerAspect: Logs the time taken to execute Service methods and Rest End points.
  
  b. ServiceValidationAspect: This aspect invokes business validations on eligible service API methods.
	 Eligible service operations are any those which:
 	-have public scope
 	-have a spring @Service annotation
 	-have a companion validator named with the form <ClassName>Validatorthat is in the "validators" package below
  	where the model object is found,
  	e.g. {@code gov.va.bip.reference.api.model.v1.validators.PersonInfoValidator.java}.
  	Validators called by this aspect should extend {@link gov.va.bip.framework.validation.AbstractStandardValidator} or
  	similar implementation.

**gov.va.bip.framework.swagger.autoconfigure**: Swagger starter and autoconfiguration to generate and configure swagger documentation

	@Configuration
	@EnableConfigurationProperties(SwaggerProperties.class)
	@EnableSwagger2
	@ConditionalOnProperty(prefix = "bip.framework.swagger", name = "enabled", matchIfMissing = true)
	@Import({ BeanValidatorPluginsConfiguration.class })
	public class SwaggerAutoConfiguration {

**gov.va.bip.framework.vault.bootstrap.autoconfigure**: Vault starter and bootstrap auto-configuration to bootstrap the Vault PropertySource as the first source loaded. This is important so that we can use the Vault generated Consul ACL token to authenticate with Consul for both Service Discovery and a K/V configuration source

	@Configuration
	@AutoConfigureOrder(1)
	@ConditionalOnProperty(prefix = "spring.cloud.vault.consul", name = "enabled", matchIfMissing = false)
	public class VaultForConsulBootstrapConfiguration implements ApplicationContextAware,
		InitializingBean {
     
## How to add dependencies in your maven pom.xml? ##
    <dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-autoconfigure</artifactId>
        <version><latest version></version>
    </dependency>


