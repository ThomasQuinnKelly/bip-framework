#!/bin/bash

# bash debug
set -x

# include environment
source /home/jenkins/.bash_profile

### This value taken from the reactor root pom ###
artifact="bip-framework-reactor"
application_version="0.0.2-SNAPSHOT"
MAVEN_BIN="mvn"
# ssc_url="http://csracitestfortify1.evss.srarad.com:8080/ssc/"

echo "====================================================="
echo " Fortify SCA for BIP Projects"
echo "`sourceanalyzer -version`"
echo "====================================================="
#read -n 1 -t 5 -p "Press a key to begin ..."

### Build the code ###
echo ">>> ${MAVEN_BIN} clean compile package -Dmaven.test.skip"
${MAVEN_BIN} clean install -Dmaven.test.skip

### Resolve the dependencies because fortify needs to have it for the scan ###
echo ">>> ${MAVEN_BIN} dependency:resolve"
${MAVEN_BIN} dependency:resolve

### Only need to worry about this jar for the scan warnings, tomcat or weblogic can take care of this dependency ###
#page_context_path="/home/jenkins/.m2/repository/javax/servlet/jsp-api/2.0/jsp-api-2.0.jar"

### Clean the SCA workspace
echo ">>> sourceanalyzer -b ${artifact} -clean"
sourceanalyzer -b ${artifact} -clean

### Build into the SCA workspace
echo ">>> sourceanalyzer -b ${artifact} touchless ${MAVEN_BIN} com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:translate -Dfortify.sca.buildId=${artifact}"
sourceanalyzer -b ${artifact} touchless ${MAVEN_BIN} com.fortify.sca.plugins.maven:sca-maven-plugin:translate -Dfortify.sca.buildId=${artifact}
#	-Dfortify.sca.cp=${page_context_path}\

	echo ">>> ${MAVEN_BIN} initialize com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:scan -Dfortify.sca.buildId=${artifact}"
${MAVEN_BIN} initialize com.fortify.sca.plugins.maven:sca-maven-plugin:scan -Dfortify.sca.buildId=${artifact}
# -Dcom.fortify.sca.fileextensions.class=BYTECODE \
	# -Dcom.fortify.sca.fileextensions.jar=ARCHIVE \
	# -DscaFailOnError=true \
	# -Dverbose=true
# ${MAVEN_BIN} initialize com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:upload \
	# 	-Dfortify.sca.buildId=${artifact} \
	#     -Dfortify.ssc.authToken=$(cat /home/jenkins/.fortify-token) \
	#     -Dfortify.ssc.url=${ssc_url} \
	#     -Dfortify.ssc.applicationName=${artifact}-parent \
	#     -Dfortify.ssc.applicationVersion=${application_version}
