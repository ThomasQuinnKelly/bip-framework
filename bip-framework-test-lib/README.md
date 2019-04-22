## What is this test library project for?

BIP Test library is customized to support testing the RESTful Service applications deployed on BIP platform. This library is configured to use broad range of operations provided by the Spring REST Client `RestTemplate`. Spring's rest template provides a convenient way to test RESTful web services by simplifying the interaction with HTTP servers.

## Dependencies:

Spring rest template, included via the `spring-web` dependency.

	    <groupId>org.springframework</groupId>
	    <artifactId>spring-web</artifactId>

cucmber, included via the `cucumber-java8` dependency. It is a library that makes it easier to write and execute parameterized test.

	   <groupId>info.cukes</groupId>
	   <artifactId>cucumber-java8</artifactId>


## How to add BIP Test Library dependencies
Add the dependency to the POM file in your `project-name-inttest` project.

	<dependency>
		<groupId>gov.va.bip.framework</groupId>
		<artifactId>bip-framework-test-lib</artifactId>
		<version><!-- add the appropriate version --></version>
	</dependency>

## Overview of the packages

Test Library uses Maven and RestTemplate for core API validations. It provides the support classes and methods that can be used to build Automated API tests for the RESTful services.

#### gov.va.bip.framework.test.rest:

`BaseStepDef`: Base class for all the step definitions.

`BaseStepDefHandler`: Handler object that extends BaseStepDef to handle rest based API call. 

#### gov.va.bip.framework.test.service:

`RestConfigService`: Loads the configuration file that has all the properties related to reference services.

`BearerTokenService`: Fetches token from the token API. The token is included in the HTTP header when invoking an end point.

#### gov.va.bip.framework.test.util:

`RestUtil`: Utility wrapper for the rest template, used to simplify making HTTP calls, parsing JSON / XML responses, and performing status code checks.

`JsonUtil`: Utility class for parsing JSON, and extract JSON snippets.

`PropertiesUtil`: Utility class for reading properties files and performing variable substitution.

`RequestResponseLoggingInterceptor`: An interceptor for all rest API calls to trace requests and responses. This interceptor can also be used for troubleshooting to trace request headers, parameters, and the response body.

## Test library example
An example of a functioning integration test can be seen in the [reference-inttest project](https://github.ec.va.gov/EPMO/bip-ocp-ref-spring-boot/tree/master/bip-reference-inttest).

## Class Diagrams
	gov.va.bip.framework.test 
<img src="/images/cd-test-lib-package.jpg" />
