# What is this project for?

BIP Framework Autoconfigure AWS Project provides application services for SQS and SNS through Localstack for the BIP platform.

# Overview of the packages

## gov.va.bip.framework.localstack.autoconfigure:

The LocalstackAutoConfiguration class defines the Localstack configuration strategy for the BIP. This configuration takes care of starting up, configuring, stopping, and cleaning up localstack upon shutdown of running the application.

It handles the configuration of the properties of the Localstack configuration based on property values retrieved from implementing projects

bip.framework.localstack
    
    enabled - required for activation because this is used for @ConditionalOnProperty for this bean and every class in these packages

localstack
    
    externalHostName - external host name for Localstack. Defaults to localhost
    pullNewImage - determines whether to pull a new image each time or not. Defaults to true
    imageTag - which localstack image to pull. Defaults to latest latest
    randomizePorts - whether to randomize the service ports for enabled services. Defaults to false

The LocalstackProperties class handles inclusion and enabling of the selected services

## gov.va.bip.framework.localstack.sns.config:

The LocalstackSnsProperties class allows for enabling and the specification of the port to use for SNS in Localstack

bip.framework.localstack.services.sns

    enabled - activates SNS for Localstack defaults to false
    port - definition of the port. Defaults to 4575

## gov.va.bip.framework.localstack.sqs.config:

The LocalstackSqsProperties class allows for enabling and the specification of the port to use for SNS in Localstack

bip.framework.localstack.services.sqs

    enabled - activates SQS for Localstack defaults to false
    port - definition of the port. Defaults to 4576
    
## resources/META-INF:

The spring.factories file enables the LocalstackAutoConfiguration. 

# Implementation for project teams 

Teams would have to include the dependecy for this module:

```xml
<dependency>
    <groupId>gov.va.bip.framework</groupId>
    <artifactId>bip-framework-autoconfigure-aws</artifactId>
    <version>${bip-framework.version}</version>
</dependency>
```

Then turn on the use Localstack and their selection of services to implement

```
---
### Localstack
bip.framework.localstack:
  enabled: true
  services:
    sns:
      enabled: true
    sqs:
      enabled: true
```
