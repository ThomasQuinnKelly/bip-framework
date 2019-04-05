This page documents the purpose and capabilities of **OpenShift Container Platform Framework Libraries** for the services.

## What is this library project for? ##

This project contains interfaces, annotations and classes consumed by the application services for various functionality as listed below
* Rest Provider Message classes, RestTemplate
* Audit and Performance Logging aspects
* Utility ops for logging and handling exceptions
* Root classes for checked and runtime exceptions
* WebService client config
* Security JWT base classes, properties and exceptions
* Service Domain Message classes, timer and validation aspects

## How to add dependency in your project?

    <dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-libraries</artifactId>
        <version><!-- add the appropriate version --></version>
    </dependency>

## Class Diagrams

##### Auditing Package
gov.va.bip.framework.audit

<img src = "/images/cd-audit-package.jpg">

##### Aspects Join Points and Pointcuts Packages
   gov.va.bip.framework.aspect <br/>
   gov.va.bip.framework.rest.provider.aspect<br/>
   gov.va.bip.framework.service.aspect<br/>
   
<img src = "/images/cd-aspect-packages.jpg">

##### Exception Packages
   gov.va.bip.framework.exception <br/>
   gov.va.bip.framework.rest.exception<br/>
   
<img src = "/images/cd-exception-packages.jpg">
