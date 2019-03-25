## What is this repository for?

To run spring boot and spring cloud enabled services on the BIP Platform, it must adhere to various service patterns. This repository contains suite of framework libraries, auto configurations, test libraries and parent POM that must be included as dependencies to enable the patterns

## Project Breakdown

1. ocp-framework-autoconfigure: Shared auto-configuration for the services to enable the patterns for audit, cache, feign, rest, security, swagger, service, vault etc

1. ocp-framework-libraries: Shared libraries for the services to provide common framework and security interfaces. 

1. ocp-framework-parentpom: Parent POM for spring boot and cloud enabled services. It provides common Maven configuration and dependencies for the suite of projects.

1. ocp-framework-test-lib: Test library framework to support functional testing for the services

## How to include and download these dependency libraries in your project

     <dependency>
         <groupId>gov.va.ocp.framework</groupId>
         <artifactId>ocp-framework-autoconfigure</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.ocp.framework</groupId>
         <artifactId>ocp-framework-libraries</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.ocp.framework</groupId>
         <artifactId>ocp-framework-parentpom</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.ocp.framework</groupId>
         <artifactId>ocp-framework-test-lib</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>

To make these libraries available locally for the service projects to compile and build, there are 3 options.

**OPTION 1**

1. Clone the OCP framework repository `git clone https://github.com/department-of-veterans-affairs/bip-ocp-framework.git`
1. Navigate to the folder `bip-ocp-framework` and run `mvn clean install` command. This would build all the libraries with versions as configured in pom.xml files.

**OPTION 2**

**If you are on VA network, the framework libraries would be made available from nexus repository with base url: https://nexus.dev.bip.va.gov/repository** You MUST have BIP Nexus url configured in the reactor POM xml file as shown below.
    
	<repositories>
		<repository>
			<id>nexus3</id>
			<name>BIP Nexus Repository</name>
			<url>https://nexus.dev.bip.va.gov/repository/maven-public</url>
		</repository>
	</repositories>
	
**OPTION 3**
**If you are NOT on VA network, a temporary solution in provided where GitHub repository acts as your nexus repository.**

Add the below section in the reactor (root) pom.xml of your service project. See example: https://github.com/department-of-veterans-affairs/ocp-reference-spring-boot/blob/master/pom.xml
 
	<repositories>
		<repository>
			<id>github</id>
			<name>GitHub Repository</name>
			<url>https://raw.github.com/department-of-veterans-affairs/ocp-framework/mvn-repo</url>
		</repository>
	</repositories>
	
You MUST also update your local ~/.m2/settings.xml as shown below. Replace values between {{Text}} with your information

	<settings>
	  <servers>
	    <server>
	      <id>github</id>
	      <username>{{Your GitHub User Name}}</username>
	      <password>{{Your Personal Access Token}}</password>
	      <configuration>
        	<httpHeaders>
	          	<property>
	            	<name>Authorization</name>
	            	<!--
			For value tag below:
				Step 1: Base64-encode your username and Github access token together
				        in the form: {{username}}:{{access_token}}
					Example: encode the string "myGithubUsername:ab123983245sldfkjsw398r7"
				Step 2: Add the encoded string to the value tag in the form of
					"Basic {{encoded-string}}"
					Example: <value>Basic YXJtaXvB4F5ghTE2OGYwNmExMWM2NDdhYjWExZjQ1N2FhNGJiMjE=</value>
	            	Base64 encoders:
				https://codebeautify.org/base64-encode
				https://www.base64encode.org/
			-->
	            	<value>Basic {{base64 encoded content}}</value>
	          	</property>
        	</httpHeaders>
          </configuration>
	    </server>
	  </servers>
	</settings>

## How to deploy and host a maven repository on GitHub

source : http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github

~/.m2/settings.xml
	
	Same format as mentioned in the previous section 

ocp-framework-parentpom/pom.xml
      
       See the section under "local-deploy" profile
	
Run command to deploy and upload artifacts to the repository
	
	mvn clean deploy -Plocal-deploy -DrepositoryName=bip-ocp-framework -DrepositoryOwner=EPMO 
         -- OR --
    mvn clean deploy -Plocal-deploy -DrepositoryName=ocp-framework -DrepositoryOwner=department-of-veterans-affairs
