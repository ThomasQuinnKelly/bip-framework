This page documents the purpose and capabilities of **BIP Framework Libraries** for the services.

## What is this library project for? ##

This project contains interfaces, annotations and classes consumed by the application services for various functionality:
* Marker interfaces for transfer objects to support common identification and behavior
* Rest Provider Message classes, RestTemplate
* Audit and Performance Logging aspects
* Utility ops for logging and handling exceptions
* Root classes for checked and runtime exceptions
* WebService client config
* Security JWT base classes, properties and exceptions
* Service Domain Message classes, timer and validation aspects

## BIP Framework principles
BIP Framework aims to:
* free developers from many of the complexities of dealing with the underlying platform,
* enable centralized application configuration,
* enable developers to focus more on business requirements and less on boilerplate code,
* encourage developers to use good coding practices and patterns that are effective and efficient,
* encourage developers to write code that presents a common "look and feel" across projects,
* enable developers to produce reliable code that takes less time to develop and test.

## How to add the Framework dependency
Add the dependency in the application project's POM file.

```xml
    <dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-libraries</artifactId>
        <version><!-- add the appropriate version --></version>
    </dependency>
```

## Framework usage in service applications

For more information about developing applications on the BIP Framework, see [Developing with BIP Framework](https://github.ec.va.gov/EPMO/bip-reference-person/tree/master/docs/developing-with-bip-framework.md).

## Sequence Diagrams

#### __Audit__

##### *@Auditable*
<img src="/images/sd-lib-audit-annotation-before.png">
<img src="/images/sd-lib-audit-annotation-afterreturning.png">
<img src="/images/sd-lib-audit-annotation-afterthrowing.png">

##### *ProviderHttpAspect*

<img src="/images/sd-lib-audit-providerhttpascpect-before.png">
<img src="/images/sd-lib-audit-providerhttpascpect-afterreturning.png">
<img src="/images/sd-lib-audit-providerhttpascpect-afterthrowing.png">

##### *BipCacheInterceptor*
<img src="/images/sd-lib-audit-cache.png">

##### *AuditLogSerializer & AuditLogger*
<img src="/images/sd-lib-audit-logserializer.png">

#### __Logger__
<img src="/images/sd-lib-log.png">

#### __JWT Token Generator__
<img src="/images/sd-lib-security-jwt-generator.png">

## Class Diagrams

#### __Aspects, Join Points and Pointcuts__
    gov.va.bip.framework.aspect
    gov.va.bip.framework.rest.provider.aspect
    gov.va.bip.framework.service.aspect
<img src="/images/cd-lib-aspect.png">

#### __Audit__
    gov.va.bip.framework.audit
    gov.va.bip.framework.audit.annotation
    gov.va.bip.framework.audit.http
    gov.va.bip.framework.audit.model
<img src="/images/cd-lib-audit.png">

#### __Cache__
    gov.va.bip.framework.cache
    gov.va.bip.framework.cache.interceptor
<img src="/images/cd-lib-cache.png">

#### __Client__
##### *REST Client*
    gov.va.bip.framework.client.rest.template
<img src="/images/cd-lib-client-rest.png">

##### *SOAP Client*
    gov.va.bip.framework.client.ws
    gov.va.bip.framework.client.ws.interceptor
    gov.va.bip.framework.client.ws.interceptor.transport
    gov.va.bip.framework.client.ws.remote
<img src="/images/cd-lib-client-ws.png">

#### __Config__
    gov.va.bip.framework.config
<img src="/images/cd-lib-config.png">

#### __Exception__
    gov.va.bip.framework.exception
    gov.va.bip.framework.exception.interceptor
    gov.va.bip.framework.rest.exception
    gov.va.bip.framework.security.jwt
    gov.va.bip.framework.service
<img src="/images/cd-lib-exception.png">

#### __Logging__
    gov.va.bip.framework.log
<img src="/images/cd-lib-log.png">

#### __Messages__
    gov.va.bip.framework.messages
<img src="/images/cd-lib-messages.png">

#### __Rest__
    gov.va.bip.framework.rest.exception
    gov.va.bip.framework.rest.provider
    gov.va.bip.framework.rest.aspect
<img src="/images/cd-lib-rest.png">

#### __Security__
    gov.va.bip.framework.security
    gov.va.bip.framework.security.model
    gov.va.bip.framework.security.util
<img src="/images/cd-lib-security.png">

#### __Security JWT__
    gov.va.bip.framework.security.jwt
    gov.va.bip.framework.security.jwt.correlation
<img src="/images/cd-lib-security-jwt.png">

#### __Service__
    gov.va.bip.framework.service
    gov.va.bip.framework.service.spect
<img src="/images/cd-lib-service.png">

#### __Swagger__
    gov.va.bip.framework.swagger
<img src="/images/cd-lib-swagger.png">

#### __Transfer__
    gov.va.bip.framework.transfer
    gov.va.bip.framework.transfer.jaxb.adapters
    gov.va.bip.framework.transfer.transform
<img src="/images/cd-lib-transfer.png">

#### __Validation__
    gov.va.bip.framework.validation
<img src="/images/cd-lib-validation.png">



