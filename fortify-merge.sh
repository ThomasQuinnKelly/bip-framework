#!/bin/bash

# bash debug
# set -x

artifactVersion=`grep -m 1 "<version>" pom.xml | cut -d "<" -f2 | rev | cut -d ">" -f1 | rev`
newFpr="target/fortify/bip-framework-reactor-$artifactVersion.fpr"
mainFpr="./bip-framework.fpr"

echo "+>> Merging $mainFpr into $newFpr"
FPRUtility -merge -project $newFpr -source $mainFpr -f $newFpr

echo "+>> Backing up $mainFpr into $mainFpr".backup
cp -fv "$mainFpr" "$mainFpr".backup

echo "+>> Copying up $newFpr over top of $mainFpr"
cp -fv "$newFpr" "$mainFpr"

echo "---------------------------------------"
echo "Merge complete."
echo "$mainFpr has been backed up, "
echo "and is ready for inspection and push."
echo ""
