# What is this project for?

BIP Framework Autoconfigure Project is a suite of POM files that provides application services with starter dependencies for the BIP platform.

# Overview of the packages

## gov/va/bip/framework/starter/logger:

The [`bip-framework-logback-starter.xml`](https://github.com/department-of-veterans-affairs/bip-framework/blob/master/bip-framework-autoconfigure/src/main/resources/gov/va/bip/framework/starter/logger/bip-framework-logback-starter.xml) defines the Logback configuration _include_ for the BIP asynchronous appender `BIP_FRAMEWORK_ASYNC_CONSOLE_APPENDER` and the json formatting `BIP_FRAMEWORK_CONSOLE_LOG_ENCODER`. This log configuration represents the log output that is routed by logstash to Kibana.

For more information, see [Log and Audit Management](https://github.com/department-of-veterans-affairs/bip-reference-person/blob/master/docs/log-audit-management.md).

The framework-supplied appender can be referenced in service [`logback-spring.xml`](https://github.com/department-of-veterans-affairs/bip-reference-person/blob/master/bip-reference-person/src/main/resources/logback-spring.xml) by including the resource:

```xml
<include resource="gov/va/bip/framework/starter/logger/bip-framework-logback-starter.xml" />
    <appender-ref ref="BIP_FRAMEWORK_ASYNC_CONSOLE_APPENDER" />
```

## gov.va.bip.framework.audit.autoconfigure:

Audit auto-configuration that provides the serializer bean and enables async execution.

```java
    @Configuration
    @EnableAsync
    public class BipAuditAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean
            public AuditLogSerializer auditLogSerializer() {
                return new AuditLogSerializer();
            }
    }
```

## gov.va.bip.framework.cache.autoconfigure:

Redis cache auto-configuration that provides property-driven beans to set up the Redis connection, start the Redis Embedded Server (if in a spring profile that requires it), and expose a JMX bean for developers to clear the cache with.

Caches are configured for a specific naming scheme of: `cacheName_ProjectName_MavenVersion`; the actual properties entry might look something like _`appName`_`Service\_@project.name@\_@project.version@`

Redis attributes are configured in the Service Application's application YAML file under `spring.redis` properties. See also [Redis configuration](https://github.com/department-of-veterans-affairs/bip-reference-person/blob/master/docs/cache-management.md#redis-configuration).

Cache-specific attributes for the application are configured in the application YAML under the `bip.framework.cache.**` property. See properties and comments under the `spring:redis:**` and the `bip.framework:cache:**` sections in [bip-reference-person.yml](https://github.com/department-of-veterans-affairs/bip-reference-person/blob/master/bip-reference-person/src/main/resources/bip-reference-person.yml).

Any properties that do not appear in the appropriate hierarchy will be silently ignored, so default values, or nulls will be substituted for properties that were believed to be configured.

Auto-configuration is declared as below. Beans created in this class refer to the other classes found in the package.

```java
    @Configuration
    @AutoConfigureAfter(CacheAutoConfiguration.class)
    @EnableCaching
    /* @Import to participate in the auto configure bootstrap process */
    @Import({ BipCachesConfig.class, BipJedisConnectionConfig.class })
    @ConditionalOnProperty(name = BipCacheAutoConfiguration.CONDITIONAL_SPRING_REDIS,
		havingValue = BipCacheAutoConfiguration.CACHE_SERVER_TYPE)
    @EnableMBeanExport(defaultDomain = "gov.va.bip", registration = RegistrationPolicy.FAIL_ON_EXISTING)
    public class BipCacheAutoConfiguration extends CachingConfigurerSupport {
         /** Domain under which JMX beans are exposed */
	    public static final String JMX_DOMAIN = "gov.va.bip";
	    /** ConditionalOnProperty property name */
	    public static final String CONDITIONAL_SPRING_REDIS = "spring.cache.type";
	    /** The cache server type */
	    public static final String CACHE_SERVER_TYPE = "redis";
        
        /** Refresh order for JedisConnectionFactory must be lower than for CacheManager */
	    static final int REFRESH_ORDER_CONNECTION_FACTORY = 1;
	    /** Refresh order for CacheManager must be higher than for JedisConnectionFactory */
	    static final int REFRESH_ORDER_CACHES = 10;
    ...
    }
```

Developers needing to clear the cache for local testing purposes have a tool available, as outlined in [Clearing the Redis Cache](https://github.com/department-of-veterans-affairs/bip-reference-person/tree/master/local-dev#clearing-the-redis-cache)

## gov.va.bip.framework.feign.autoconfigure:

Feign client auto-configuration creates a number of beans to support RESTful client calls through the feign library:

- Hystrix enablement for Feign clients. The `feignBuilder` bean defines Hystrix properties like Group Key, Threading strategy, etc.
- `FeignCustomErrorDecoder` has been created to interrogate and modify the Exception being propagated.

```java
@Configuration
        public class BipFeignAutoConfiguration {
        ...
        }
```

## gov.va.bip.framework.hystrix.autoconfigure:

Hystrix auto-configuration sets up Hystrix with the THREAD strategy. The configuration copies RequestAttributes from ThreadLocal to Hystrix threads in the `RequestAttributeAwareCallableWrapper` bean. This is done to make sure the necessary request information is available on the Hystrix thread.

```java
    @Configuration
    @ConditionalOnProperty(value = "hystrix.wrappers.enabled", matchIfMissing = true)
    public class HystrixContextAutoConfiguration {
    ...
    }
```

## gov.va.bip.framework.rest.autoconfigure:

REST auto-configuration creates beans to enable a number of capabilities related to RESTful clients and providers.

- `restClientTemplate` is a customized bean that acts as an alternative to using the Feign client.
- `tokenClientHttpRequestInterceptor` passes the JWT token from Request to Response objects as they pass through the interceptor.
- `BipRestGlobalExceptionHandler` is configured to handle exceptions from server to client and modify them (if needed) for appropriate communication to the consumer.
- `ProviderHttpAspect` audits requests and responses passing throught the provider.
- `RestProviderTimerAspect` logs performance data using `PerformanceLoggingAspect`.

```java
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
```

## gov.va.bip.framework.security.autoconfigure:

Security auto-configuration creates beans for the security framework using JWT.

- `JwtWebSecurityConfigurerAdapter` provides configuration for JWT security processing and provides configuration like filters need to be used to Authenticate, URL's to be processed etc.

```java
    @Configuration
    @ConditionalOnProperty(prefix = "bip.framework.security.jwt", name = "enabled", matchIfMissing = true)
    @Order(JwtAuthenticationProperties.AUTH_ORDER)
    protected static class JwtWebSecurityConfigurerAdapter
            extends WebSecurityConfigurerAdapter {
    ...
    }
```

- `JwtWebSecurityConfigurerAdapter` defines beans below and their respective uses: a. AuthenticationEntryPoint - Returns an error message when a request does not authenticate.

  b. AuthenticationProvider - Decrypts and parses the JWT.

  c. JwtAuthenticationSuccessHandler - Providea an oppportunity for any post-authentication processing.

  d. JwtAuthenticationFilter - Bean is central to security configuration: configures the springboot starter for the platform security using the above beans. It also audits the JWT Token.

- `TokenResource` is used to expose an end point for Token Generation on the Swagger page.

```java
@Bean
        @ConditionalOnMissingBean
        @ConditionalOnExpression("${bip.framework.security.jwt.enabled:true} && ${bip.framework.security.jwt.generate.enabled:true}")
        public TokenResource tokenResource() {
        ...
        }
```

## gov.va.bip.framework.service.autoconfigure:

Service auto-configuration configures beans that get used in service applications, including:

- ServiceTimerAspect: Logs the time taken to execute Service methods and Rest End points.
- ServiceValidationAspect: invokes business validations on eligible service interface methods. Eligible service operations are any those which:

  - have public scope
  - have a spring @Service annotation
  - have a companion validator named with the form `\<ClassName\>Validator` that is in the "validators" package below where the model object is found, for example `gov.va.bip.reference.api.model.v1.validators.PersonInfoValidator.java`.
  - Validators called by this aspect should extend `gov.va.bip.framework.validation.AbstractStandardValidator` or similar implementation.

```java
        @Configuration
        public class BipServiceAutoConfiguration {
        ...
        }
```

## gov.va.bip.framework.swagger.autoconfigure:

Swagger starter and autoconfiguration to generate and configure swagger documentation:

```java
  @Configuration
  @EnableWebMvc
  @ConditionalOnProperty(prefix = "bip.framework.swagger", name = "enabled", matchIfMissing = true)
  public class BipSwaggerAutoConfiguration implements WebMvcConfigurer {
    ...
    }
```

## gov.va.bip.framework.validator.autoconfigure:

Validator auto-configuration enables the standard JSR 303 validator (useful for model validation in REST controllers, for example). `LocalValidatorFactoryBean` is created to allow further customization of the validator's behaviour.

```java
@Configuration
    @AutoConfigureBefore(MessageSourceAutoConfiguration.class)
    public class BipValidatorAutoConfiguration {
    ...
    }
```

## gov.va.bip.framework.vault.bootstrap.autoconfigure:

Vault starter and bootstrap auto-configuration to bootstrap the Vault PropertySource as the first source loaded. This is important so that we can use the Vault generated Consul ACL token to authenticate with Consul for both Service Discovery and a K/V configuration source.

```java
@Configuration
    @AutoConfigureOrder(1)
    @ConditionalOnProperty(prefix = "spring.cloud.vault.consul", name = "enabled", matchIfMissing = false)
    public class VaultForConsulBootstrapConfiguration implements ApplicationContextAware,
        InitializingBean {
    ...
    }
```

# How to add dependencies in your maven pom.xml?

Standard maven dependency configuration.

```xml
<dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-autoconfigure</artifactId>
        <version><latest version></version>
    </dependency>
```

# Class Diagrams

## Audit Autoconfigure

```
gov.va.bip.framework.audit.autoconfigure
```

![](/images/cd-autoconf-audit.png)

## Cache Autoconfigure

```
gov.va.bip.framework.cache.autoconfigure
```

![](/images/cd-autoconf-cache.png)

## Feign Autoconfigure

```
gov.va.bip.framework.feign.autoconfigure
```

![](/images/cd-autoconf-feign.png)

## Hystrix Autoconfigure

```
gov.va.bip.framework.hystrix.autoconfigure
```

![](/images/cd-autoconf-hystrix.png)

## REST Autoconfigure

```
gov.va.bip.framework.rest.autoconfigure
```

![](/images/cd-autoconf-rest.png)

## Security Autoconfigure

```
gov.va.bip.framework.security.autoconfigure
```

![](/images/cd-autoconf-security.png)

## Service Autoconfigure

```
gov.va.bip.framework.service.autoconfigure
```

![](/images/cd-autoconf-service.png)

## Swagger Autoconfigure

```
gov.va.bip.framework.swagger.autoconfigure
```

![](/images/cd-autoconf-swagger.png)

## Validator Autoconfigure

```
gov.va.bip.framework.validator.autoconfigure
```

![](/images/cd-autoconf-validator.png)

## Vault Autoconfigure

```
gov.va.bip.framework.vault.bootstrap.autoconfigure
```

![](/images/cd-autoconf-vault.png)
