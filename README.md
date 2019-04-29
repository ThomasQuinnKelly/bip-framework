## What is this repository for?

To run spring boot and spring cloud enabled services on the BIP Platform, it must adhere to various service patterns. This repository contains a suite of framework libraries, auto configurations, test libraries and parent POM that must be included as dependencies to enable the patterns.

For general information regarding recommended development patterns for developing service applications, and the purpose and usage of capabilities that are provided by the BIP Framework, see the [bip-reference-spring-boot README.md](https://github.ec.va.gov/EPMO/bip-ocp-ref-spring-boot).

## Project Breakdown & Links

1. [bip-framework-autoconfigure](bip-framework-autoconfigure/README.md): Shared auto-configuration for the services to enable the patterns for audit, cache, feign, rest, security, swagger, service, vault etc.

1. [bip-framework-libraries](bip-framework-libraries/README.md): Shared libraries for the services to provide common framework and security interfaces. 

1. [bip-framework-parentpom](bip-framework-parentpom/README.md): Parent POM for spring boot and cloud enabled services. It provides common Maven configuration and dependencies for the suite of projects.

1. [bip-framework-test-lib](bip-framework-test-lib/README.md): Test library framework to support functional testing for service applications.

## How to include BIP Framework libraries in your project

```xml
     <dependency>
         <groupId>gov.va.bip.framework</groupId>
         <artifactId>bip-framework-autoconfigure</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.bip.framework</groupId>
         <artifactId>bip-framework-libraries</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.bip.framework</groupId>
         <artifactId>bip-framework-parentpom</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
       <dependency>
         <groupId>gov.va.bip.framework</groupId>
         <artifactId>bip-framework-test-lib</artifactId>
         <version><!-- add the appropriate version --></version>
       </dependency>
```

## How to download the BIP Framework maven dependencies
There are currently three ways to make these projects available for local activites.

#### Option 1 - clone the source from GitHub
This will bring the framework source code to your local machine.

1. Clone the BIP framework repository:

	 `git clone https://github.ec.va.gov/EPMO/bip-ocp-framework.git`

2. Navigate to the `bip-framework` folder, and run:

```	mvn clean install```

This will build all the libraries with versions as configured in `pom.xml` files.

#### Option 2 - get the JAR from the VA nexus repo
This option is viable **only** if you are working on a machine that is connected to the VA network.

The base URL for the nexus repository is: https://nexus.dev.bip.va.gov/repository 

You must have the BIP Nexus URL configured in the reactor (root) `pom.xml` file as shown below.
    
```xml
	<repositories>
		<repository>
			<id>nexus3</id>
			<name>BIP Nexus Repository</name>
			<url>https://nexus.dev.bip.va.gov/repository/maven-public</url>
		</repository>
	</repositories>
```

#### Option 3 - use GitHub as a Nexus repository
This option is a workaround if you are *not* on the VA network, and you only want the BIP Framework JAR.

This is a temporary solution. The GitHub repository acts as your Nexus repository.
It requires changes in the reactor POM, and in your local maven settings file, as shown below.

1. **POM file**

Add the section shown below to the reactor (root) `pom.xml` of your service project. An example of this configuration can be seen in the [bip-reference reactor POM](https://github.ec.va.gov/EPMO/bip-ocp-ref-spring-boot/blob/master/pom.xml).
 
```xml
	<repositories>
		<repository>
			<id>github</id>
			<name>GitHub Repository</name>
			<url>https://raw.github.com/department-of-veterans-affairs/bip-framework/mvn-repo</url>
		</repository>
	</repositories>
```

2. **Settings file**

Update your local `~/.m2/settings.xml` as shown below. Replace values between `{{Text}}` with your information.

```xml
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
```

## How to deploy and host a maven repository on GitHub

source : http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github

1. Modify `~/.m2/settings.xml`, same format as mentioned in the previous section 

2. Update your application's parent pom with a `local-deploy` profile that configures your GitHub repo. For an example, look in [bip-framework-parentpom/pom.xml](https://github.ec.va.gov/EPMO/bip-ocp-framework/blob/master/bip-framework-parentpom/pom.xml) for the section framed by:

```xml
	<profile>
		<id>local-deploy</id>
			...    ...
	</profile>
```

3. Run maven with the appropriate parameters to deploy and upload artifacts to the repository

```	mvn clean deploy -Plocal-deploy -DrepositoryName=bip-ocp-framework -DrepositoryOwner=EPMO ```

-- OR --

	mvn clean deploy -Plocal-deploy -DrepositoryName=bip-framework -DrepositoryOwner=department-of-veterans-affairs
