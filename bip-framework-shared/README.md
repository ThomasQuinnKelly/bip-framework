This page documents the purpose and capabilities of **BIP Framework Libraries** for BIP micro-services.

## What is this library project for? ##

This project contains utilities and functional helpers that can be shared freely with any java project. 
* Sanitizer
* ... more to come

## Shared library principles
The shared library is intended to:
* provide common utilities and helpers for any project that might benefit from them - think of this jar as you might the apache-commons libraries
* have **no** dependencies on BIP projects, private repos, `gov.**` projects, or 3rd-party multimodule projects (e.g. spring, hibernate, etc) - dependencies should be limited to `java.**` and 3rd-party single jar libraries e.g. apache-commons, etc
* emit exceptions that are (or are extended from) the core `java.lang.Exception` types - **do not** use BipExceptionExtender or its derivatives; add handlers to `BipRestGlobalExceptionHandler` to provide appropriate messages on the response

## How to add the Shared Library dependency
Add the dependency in the application project's POM file.

```xml
<dependency>
    <groupId>gov.va.bip.framework</groupId>
    <artifactId>bip-framework-shared</artifactId>
    <version><!-- add the appropriate version --></version>
</dependency>
```

## Class Diagrams
##### Sanitizer
<img src="/images/cd-bip.framework.shared.sanitize.png">
