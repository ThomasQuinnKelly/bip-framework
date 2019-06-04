#!/bin/bash

### bash debug
# set -x

### log file name
logfile="`pwd`/fortify-sca.log"
### include environment
source /home/jenkins/.bash_profile

### This value taken from the reactor root pom
artifact="bip-framework-reactor"
### artifactVersion is not currently used, but will be necessary for maven integration
artifactVersion=`grep -m 1 "<version>" pom.xml | cut -d "<" -f2 | rev | cut -d ">" -f1 | rev`
### the maven command
MAVEN_BIN="mvn"

### output header info, get the log started
echo "=====================================================" 2>&1 | tee "$logfile"
echo " Fortify SCA for BIP Projects" 2>&1 | tee -a "$logfile"
echo "Artifact version: $artifactVersion" 2>&1 | tee -a "$logfile"
echo "SCA version: `sourceanalyzer -version`" 2>&1 | tee -a "$logfile"
echo "=====================================================" 2>&1 | tee -a "$logfile"

### rudimentary checks for valid current working directory
if [ ! -f "./pom.xml" ] || [ "artifactVersion" == "" ] || [ ! -d "./bip-framework-libraries" ] || [ ! -f "./bip-framework-libraries/pom.xml" ]; then
	echo "" 2>&1 | tee -a "$logfile"
	echo "*** ERROR ***" 2>&1 | tee -a "$logfile"
	echo "*** './bip-framework-libraries' does not exist," 2>&1 | tee -a "$logfile"
	echo "*** or could not extract artifact version from ./pom.xml" 2>&1 | tee -a "$logfile"
	echo "*** This script must be run on a viable bip-framework clone" 2>&1 | tee -a "$logfile"
	echo "*** and must be run from within the projects root directory." 2>&1 | tee -a "$logfile"
	echo "Logs in: $logfile" 2>&1 | tee -a "$logfile"
	echo "" 2>&1 | tee -a "$logfile"
	exit 111
fi

### Build the code
echo "+>> ${MAVEN_BIN} clean install package -DskipTests=true" 2>&1 | tee -a "$logfile"
${MAVEN_BIN} clean install -DskipTests=true 2>&1 >> "$logfile"

### Resolve the dependencies because fortify needs to have it for the scan
echo "+>> ${MAVEN_BIN} dependency:resolve" 2>&1 | tee -a "$logfile"
${MAVEN_BIN} dependency:resolve 2>&1 >> "$logfile"

### Clean the SCA workspace
echo "+>> sourceanalyzer -b ${artifact} -clean" 2>&1 | tee -a "$logfile"
sourceanalyzer -b ${artifact} -clean 2>&1 >> "$logfile"

### Build into the SCA workspace
echo "+>> sourceanalyzer -b ${artifact} touchless ${MAVEN_BIN} com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:translate -Dfortify.sca.buildId=${artifact}" 2>&1 | tee -a "$logfile"
sourceanalyzer -b ${artifact} touchless ${MAVEN_BIN} com.fortify.sca.plugins.maven:sca-maven-plugin:translate -Dfortify.sca.buildId=${artifact} 2>&1 >> "$logfile"

### Perform the scan
echo "+>> ${MAVEN_BIN} initialize com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:scan -Dfortify.sca.buildId=${artifact}" 2>&1 | tee -a "$logfile"
${MAVEN_BIN} initialize com.fortify.sca.plugins.maven:sca-maven-plugin:scan -Dfortify.sca.buildId=${artifact} 2>&1 >> "$logfile"

## done
echo "---------------------------------------" 2>&1 | tee -a "$logfile"
echo "Fortify Scan complete." 2>&1 | tee -a "$logfile"
echo "The new FPR can be found at target/fortify/*.fpr" 2>&1 | tee -a "$logfile"
echo "Confirm success, then run './fortify-merge.sh'" 2>&1 | tee -a "$logfile"
echo "Logs in: $logfile" 2>&1 | tee -a "$logfile"
echo "" 2>&1 | tee -a "$logfile"
