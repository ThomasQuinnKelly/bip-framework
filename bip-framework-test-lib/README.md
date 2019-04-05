## What is this test library project for? ##

Test -lib test Automation Framework is customized to support testing the REST Services deployed on BIP platform. It supports testing of REST protocols with the help of REST template. Spring RestTemplate provides a convenient way to test RESTful web services. It simplifies the interaction with HTTP servers and enforces RESTful systems.

## Dependencies:

Rest template - This is the Rest-template library itself.
   
			<groupId>org.springframework</groupId>
			<artifactId>spring-web</artifactId>

cucumber-java - a library that makes it easier for us to write and execute parameterized test.

       <groupId>info.cukes</groupId>
	   <artifactId>cucumber-java8</artifactId>


## How to add dependencies in your maven pom.xml? ##
        
          <dependency>
             <groupId>gov.va.bip.framework</groupId>
	     <artifactId>bip-framework-test-lib</artifactId>
	     <version><!-- add the appropriate version --></version>
	   </dependency>


## Overview of the packages ##

Test-lib Test Framework uses Java - Maven platform, the REST-template  for core API validations.

This folder has all the support classes and methods that can be re-used to build Automated API tests for each API.

**gov.va.bip.framework.test.rest**:

BaseStepDef: Base class for all the step definition.

BaseStepDefHandler: Handler object that extends BaseStepDef to handle rest based api call. Step definition class inject this object thru constructor.

**gov.va.bip.framework.test.service**:

RestConfigService: Loads the config file that has all the config related to reference services.

BearerTokenService: It Fetches token from the token API. The token will be used as a header while invoking actual endpoints.

**gov.va.bip.framework.test.util**:

RestUtil: It is a wrapper for rest template API for making HTTP calls, parse JSON and XML responses and status code check.

JsonUtil: Utility class for parsing JSON.

PropertiesUtil: Utilities for handling properties.
