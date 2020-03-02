# What is this project for?

BIP Framework Autoconfigure AWS Project provides application services for SQS and SNS through Localstack for the BIP platform.

# Overview of the packages

# SQS and SNS Autoconfiguration Rundown

The following classes declare beans for their respective class implementation of services while importing the abstract and standard configurations as well:

gov/va/bip/framework/aws/autoconfigure/BipSnsAutoConfiguration.java
gov/va/bip/framework/aws/autoconfigure/BipSqsAutoConfiguration.java

These abstractions and standard configurations handle dealing with AWS authentication and url resolutions for the topic or queue:

gov/va/bip/framework/sns/config/AbstractSnsConfiguration.java
gov/va/bip/framework/sns/config/StandardSnsConfiguration.java

gov/va/bip/framework/sqs/config/AbstractSqsConfiguration.java
gov/va/bip/framework/sqs/config/StandardSqsConfiguration.java

In the case of SQS there is a class for resolving JMS destinations. JMS is used as a wrapper around SQS offerings:

gov/va/bip/framework/sqs/config/StaticDestinationResolver.java

These properties classes are used by Localstack to configure the topic and queue and the subscription from the topic to the queue:

gov/va/bip/framework/sns/config/SnsProperties.java
gov/va/bip/framework/sns/config/SnsTopicProperties.java

gov/va/bip/framework/sqs/config/SqsProperties.java
gov/va/bip/framework/sqs/config/SqsQueueProperties.java

# SQS and SNS Service Rundown

These service interface and implementation classes provide the following methods:

gov/va/bip/framework/sqs/services/impl/SqsServiceImpl.java
gov/va/bip/framework/sqs/services/SqsService.java

* createTextMessage - creates a message through connection factory, in preparation for sending it to a queue. 
* sendMessage - delivers a prepared message to the queue

gov/va/bip/framework/sns/services/impl/SnsServiceImpl.java
gov/va/bip/framework/sns/services/SnsService.java

* publish - sends the message content to the topic.

# Localstack Rundown

## gov.va.bip.framework.localstack.autoconfigure:

The LocalstackAutoConfiguration class defines the Localstack configuration strategy for the BIP. This configuration takes care of starting up, configuring, stopping, and cleaning up localstack upon shutdown of running the application.

It handles the configuration of the properties of the Localstack configuration based on property values retrieved from implementing projects

bip.framework.localstack
    
    enabled - required for activation because this is used for @ConditionalOnProperty for this bean and every class in these packages
    
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

The spring.factories file enables the LocalstackAutoConfiguration, BipSqsAutoConfiguration, and BipSnsAutoConfiguration. 

# Implementation for project teams 

Teams would have to include the dependecy for this module in the application project pom.xml:

```xml
<!-- Include the bip framework autoconfigure aws -->
<dependency>
    <groupId>gov.va.bip.framework</groupId>
    <artifactId>bip-framework-autoconfigure-aws</artifactId>
    <version>${bip-framework.version}</version>
</dependency>
<!-- Neccesary for some JMS opperations if implementing SQS  -->
<dependency>
    <groupId>javax.jms</groupId>
    <artifactId>javax.jms-api</artifactId>
    <version>2.0.1</version>
</dependency>
<!-- Helpful for overcoming fortify issues with messaging -->
<dependency>
    <groupId>com.mikesamuel</groupId>
    <artifactId>json-sanitizer</artifactId>
    <version>1.2.0</version>
</dependency>
```

Apply the embedded-aws profile for local scenarios.

```
spring.profiles.include: ... embedded-aws
```
Turn on the use Localstack and their selection of services to implement.
```
---
### Localstack
bip.framework.localstack:
  enabled: true
  services:
    sns:
      enabled: true
      port: 4575
    sqs:
      enabled: true
      port: 4576
```
Set values for creation of resources in Localstack (for local situations). It is intended that application properties for upper environments would have the proper values for resources created in AWS. For SQS Localstack setup certain defaults are set internal to the code which match the defaults of AWS SQS Queue creation. 
```
---
### AWS Service settings
### SQS and SNS Configurations for Bip Reference Person
bip.framework:
  aws:
    sqs:
      enabled: true
      #region: us-east-1

      endpoint: http://localhost:4576/queue/sub_new_queue
      #delay: 0
      #maxMessageSize: "262144"
      #messageRetentionPeriod: "345600"
      #waitTime: 0
      #visibilityTimeout: 30

      dlqEnabled: true
      dlqEndpoint: http://localhost:4576/queue/sub_new_queue_dead
      #maxReceiveCount: 1
      #dlqdelay: 0
      #dlqmaxMessageSize: "262144"
      #dlqmessageRetentionPeriod: "345600"
      #dlqwaitTime: 0
      #dlqvisibilityTimeout: 30

      #For SQS Configuration - Messaging Provider Configuration
      #numberOfMessagesToPrefetch: 0

      #For Message Listener Setup in QueueAsyncMessageReceiver
      #Defines the maximum number of times the message can enter the DLQ
      retries: 1

      #SQS key and id needs to be changed for upper environments
      #access_key_id: ${AWS_ACCESS_KEY}
      #secret_access_key: ${AWS_SECRET_ACCESS_KEY}
    sns:
      enabled: true
      region: us-east-1
      endpoint: http://localhost:4575/topic/test_my_topic
      topicarn: arn:aws:sns:us-east-1:000000000000:test_my_topic
      name: test_my_topic
      message: "SNS Test Message"
      type: String

      #SNS key and id needs to be changed for upper environments
      #access_key_id: ${AWS_ACCESS_KEY}
      #secret_access_key: ${AWS_SECRET_ACCESS_KEY}
```
Also note the required settings at the bottom.
```
# Required or else errors occur (Spring requirement)
cloud.aws.stack.auto: false
cloud.aws.region.static: us-east-1

# Required for localstack SQS integration
spring.sleuth.messaging.enabled: false
```

# Local Integration Setup 

Implementing project teams would need to simply add another service to the docker-compose.yml

In the bip-reference-person the relevant part of the docker-compose.yml looks like this:

```
  bip-reference-person:
    image: bipdev/bip-reference-person
    build:
      context: ./bip-reference-person
      dockerfile: Dockerfile.local
    environment:
      - spring.profiles.active=local-int
      ...
      - JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n #Remote Debugging
    ports:
      - "8080:8080"
      - "8000:8000" #Remote Debugging
    networks:
      - bip
    links:
      - localstack
    depends_on:
      ...
      - localstack

  localstack:
    image: localstack/localstack:0.10.7
    container_name: localstack
    ports:
      - "4567-4584:4567-4584"
      - "8888:8888"
    environment:
      - HOSTNAME_EXTERNAL=localstack
      - DEBUG=1
      - USE_SSL=0
      - SERVICES=sns:4575,sqs:4576
      - PORT_WEB_UI=8888
    networks:
      - bip
```