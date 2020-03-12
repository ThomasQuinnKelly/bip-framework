# Overview of the package

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
