## What is this test library project for?

BIP Test library is customized to support testing the REST based Services deployed on BIP platform. This library is configured to use broad range of operations provided by Spring REST Client – RestTemplate. Spring RestTemplate provides a convenient way to test RESTful web services. It simplifies the interaction with HTTP servers and enforces RESTful systems.

## Dependencies:

Spring Rest template - Included via `spring-web` dependency.

	    <groupId>org.springframework</groupId>
	    <artifactId>spring-web</artifactId>

cucumber-java8 - a library that makes it easier for us to write and execute parameterized test.

	   <groupId>info.cukes</groupId>
	   <artifactId>cucumber-java8</artifactId>


## How to add BIP Test Library dependencies
Add the dependency to your test projects POM file.

	<dependency>
		<groupId>gov.va.bip.framework</groupId>
		<artifactId>bip-framework-test-lib</artifactId>
		<version><!-- add the appropriate version --></version>
	</dependency>

## Overview of the packages

Test Library uses Java - Maven platform, the REST-template for core API validations.

This library has the support classes and methods that can be used to build Automated API tests for the REST API services.

**gov.va.bip.framework.test.rest**:

BaseStepDef: Base class for all the step definitions.

BaseStepDefHandler: Handler object that extends BaseStepDef to handle rest based API call. 

**gov.va.bip.framework.test.service**:

RestConfigService: Loads the configuration file that has all the properties related to reference services.

BearerTokenService: Fetches token from the token API. The token will be used as a header while invoking actual end points.

**gov.va.bip.framework.test.util**:

RestUtil: It's a wrapper for rest template API to make HTTP calls, parse JSON / XML responses and do status code check.

JsonUtil: Utility class for parsing JSON. We could use this utility to parse and extract JSON snippet.

PropertiesUtil: Utility class for handling properties.

RequestResponseLoggingInterceptor: It is an interceptor for all rest API calls to trace request and responses. This interceptor can also be used as a troubleshooting technique to trace request headers, parameter and response body.

In the `bip-reference-inttest` project, `bip-framework-test-lib` is added as a dependency. This project `bip-reference-inttest` can be used as a reference for writing functional tests https://github.ec.va.gov/EPMO/bip-ocp-ref-spring-boot/tree/master/bip-reference-inttest

## Class Diagrams
	gov.va.bip.framework.test 
<img src="/images/cd-test-lib-package.jpg" />
