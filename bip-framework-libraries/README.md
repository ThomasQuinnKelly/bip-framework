This page documents the purpose and capabilities of _BIP Framework Libraries_ for the services.

# What is this library project for?

This project contains interfaces, annotations and classes consumed by the application services for various functionality:

- Marker interfaces for transfer objects to support common identification and behavior
- Rest Provider Message classes, RestTemplate
- Audit and Performance Logging aspects
- Utility ops for logging and handling exceptions
- Root classes for checked and runtime exceptions
- WebService client config
- Security JWT base classes, properties and exceptions
- Service Domain Message classes, timer and validation aspects

# BIP Framework principles

BIP Framework aims to:

- free developers from many of the complexities of dealing with the underlying platform,
- enable centralized application configuration,
- enable developers to focus more on business requirements and less on boilerplate code,
- encourage developers to use good coding practices and patterns that are effective and efficient,
- encourage developers to write code that presents a common "look and feel" across projects,
- enable developers to produce reliable code that takes less time to develop and test.

# How to add the Framework dependency

Add the dependency in the application project's POM file.

```xml
<dependency>
    <groupId>gov.va.bip.framework</groupId>
    <artifactId>bip-framework-libraries</artifactId>
    <version><!-- add the appropriate version --></version>
</dependency>
```

# Framework usage in service applications

For more information about developing applications on the BIP Framework, see [Developing with BIP Framework](https://github.ec.va.gov/EPMO/bip-reference-person/tree/master/docs/developing-with-bip-framework.md).

# Log Masking

Logback is configured in [`bip-framework-logback-starter.xml`](https://github.ec.va.gov/EPMO/bip-framework/blob/master/bip-framework-autoconfigure/src/main/resources/gov/va/bip/framework/starter/logger/bip-framework-logback-starter.xml). As the app starts up, logback's `ContextInitializer.configureByResource(..)` method reads the configured `BipMaskingMessageProvider` encoder provider. Logback invokes this custom provider by convention: the tag names and values within the `<provider>` xml declaration are used to infer java class names and properties used by the provider.

The framework uses masking rules to provide default masking for the `BIP_FRAMEWORK_ASYNC_CONSOLE_APPENDER`. See the [_Logger_](#logger) sequence diagram below.

Additional log masking definitions can be declared within services with the `BipMaskingFilter` class. This class can be referenced to declare masking in a logback filter.

If declarative masking in logback config is not sufficient for specific data, developers can manually mask data with methods from `MaskUtils`.

See [Log and Audit Management](https://github.ec.va.gov/EPMO/bip-reference-person/blob/master/docs/log-audit-management.md) for more information.

# Sequence Diagrams

## _Audit_

### _@Auditable_

![](/images/sd-lib-audit-annotation-before.png) ![](/images/sd-lib-audit-annotation-afterreturning.png) ![](/images/sd-lib-audit-annotation-afterthrowing.png)

### _ProviderHttpAspect_

![](/images/sd-lib-audit-providerhttpascpect-before.png) ![](/images/sd-lib-audit-providerhttpascpect-afterreturning.png) ![](/images/sd-lib-audit-providerhttpascpect-afterthrowing.png)

### _BipCacheInterceptor_

![](/images/sd-lib-audit-cache.png)

### _AuditLogSerializer & AuditLogger_

![](/images/sd-lib-audit-logserializer.png)

## _Logger_

![](/images/sd-lib-log.png)

## _JWT Token Generator_

![](/images/sd-lib-security-jwt-generator.png)

# Class Diagrams

## _Aspects, Join Points and Pointcuts_

```
gov.va.bip.framework.aspect
gov.va.bip.framework.rest.provider.aspect
gov.va.bip.framework.service.aspect
```

![](/images/cd-lib-aspect.png)

## _Audit_

```
gov.va.bip.framework.audit
gov.va.bip.framework.audit.annotation
gov.va.bip.framework.audit.http
gov.va.bip.framework.audit.model
```

![](/images/cd-lib-audit.png)

## _Cache_

```
gov.va.bip.framework.cache
gov.va.bip.framework.cache.interceptor
```

![](/images/cd-lib-cache.png)

## _Client_

### _REST Client_

```
gov.va.bip.framework.client.rest.template
```

![](/images/cd-lib-client-rest.png)

### _SOAP Client_

```
gov.va.bip.framework.client.ws
gov.va.bip.framework.client.ws.interceptor
gov.va.bip.framework.client.ws.interceptor.transport
gov.va.bip.framework.client.ws.remote
```

![](/images/cd-lib-client-ws.png)

## _Config_

```
gov.va.bip.framework.config
```

![](/images/cd-lib-config.png)

## _Exception_

```
gov.va.bip.framework.exception
gov.va.bip.framework.exception.interceptor
gov.va.bip.framework.rest.exception
gov.va.bip.framework.security.jwt
gov.va.bip.framework.service
```

![](/images/cd-lib-exception.png)

## _Logging_

```
gov.va.bip.framework.log
```

![](/images/cd-lib-log.png)

## _Messages_

```
gov.va.bip.framework.messages
```

![](/images/cd-lib-messages.png)

## _Rest_

```
gov.va.bip.framework.rest.exception
gov.va.bip.framework.rest.provider
gov.va.bip.framework.rest.aspect
```

![](/images/cd-lib-rest.png)

## _Security_

```
gov.va.bip.framework.security
gov.va.bip.framework.security.model
gov.va.bip.framework.security.util
```

![](/images/cd-lib-security.png)

## _Security JWT_

```
gov.va.bip.framework.security.jwt
gov.va.bip.framework.security.jwt.correlation
```

![](/images/cd-lib-security-jwt.png)

## _Service_

```
gov.va.bip.framework.service
gov.va.bip.framework.service.spect
```

![](/images/cd-lib-service.png)

## _Swagger_

```
gov.va.bip.framework.swagger
```

![](/images/cd-lib-swagger.png)

## _Transfer_

```
gov.va.bip.framework.transfer
gov.va.bip.framework.transfer.jaxb.adapters
gov.va.bip.framework.transfer.transform
```

![](/images/cd-lib-transfer.png)

## _Validation_

```
gov.va.bip.framework.validation
```

![](/images/cd-lib-validation.png)
