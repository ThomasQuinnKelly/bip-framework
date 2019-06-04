#!/bin/bash

### bash debug
# set -x

### log file name
logfile="`pwd`/fortify-merge.log"
### required to find the latest fpr in the target directory
artifactVersion=`grep -m 1 "<version>" pom.xml | cut -d "<" -f2 | rev | cut -d ">" -f1 | rev`
### the latest fpr in the target directory
newFpr="./target/fortify/bip-framework-reactor-$artifactVersion.fpr"
### the permanent fpr in the root directory
mainFpr="./bip-framework.fpr"

### output header info, get the log started
echo "=====================================================" 2>&1 | tee "$logfile"
echo " Fortify Merge for BIP Projects" 2>&1 | tee -a "$logfile"
echo "Artifact version: $artifactVersion" 2>&1 | tee -a "$logfile"
echo "SCA version: `sourceanalyzer -version`" 2>&1 | tee -a "$logfile"
echo "=====================================================" 2>&1 | tee -a "$logfile"

### check for valid FPRs
if [ "artifactVersion" == "" ] || [ ! -f "$newFpr" ] || [ "$newFpr" -ot "$mainFpr" ]; then
	echo "" 2>&1 | tee -a "$logfile"
	echo "*** ERROR ***" 2>&1 | tee -a "$logfile"
	echo "*** 'target/fortify/*.fpr' does not exist or is old," 2>&1 | tee -a "$logfile"
	echo "*** or could not extract artifact version from ./pom.xml" 2>&1 | tee -a "$logfile"
	echo "*** This script must be run on a viable bip-framework clone" 2>&1 | tee -a "$logfile"
	echo "*** and must be run from within the projects root directory." 2>&1 | tee -a "$logfile"
	echo "*** Run './fortify-sca.sh' first! ***" 2>&1 | tee -a "$logfile"
	echo "Logs in: $logfile" 2>&1 | tee -a "$logfile"
	echo "" 2>&1 | tee -a "$logfile"
	exit 111
fi

## merge the permanent FPR into the latest FPR
echo "+>> Merging $mainFpr into $newFpr" 2>&1 | tee -a "$logfile"
FPRUtility -merge -project $newFpr -source $mainFpr -f $newFpr 2>&1 >> "$logfile"

## back up a local copy of the permanent FPR
echo "+>> Backing up $mainFpr into $mainFpr".backup 2>&1 | tee -a "$logfile"
cp -fv "$mainFpr" "$mainFpr".backup 2>&1 >> "$logfile"

## copy the new FPR over top of the old permanent FPR
echo "+>> Copying up $newFpr over top of $mainFpr" 2>&1 | tee -a "$logfile"
cp -fv "$newFpr" "$mainFpr" 2>&1 >> "$logfile"

## done
echo "---------------------------------------" 2>&1 | tee -a "$logfile"
echo "Merge complete." 2>&1 | tee -a "$logfile"
echo "$mainFpr has been backed up, " 2>&1 | tee -a "$logfile"
echo "and is ready for inspection and push." 2>&1 | tee -a "$logfile"
echo "Logs in: $logfile" 2>&1 | tee -a "$logfile"
echo "" 2>&1 | tee -a "$logfile"
