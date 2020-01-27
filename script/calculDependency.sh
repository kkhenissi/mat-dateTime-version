#!/bin/bash


SCRIPT=$(readlink -f "$0")
SCRIPTDIR=$(dirname "$SCRIPT")


TARGET=$SCRIPTDIR/../target/dependencies
rm -rf ${TARGET}
mkdir -p ${TARGET}

cd $SCRIPTDIR/..

./mvnw --quiet dependency:tree -DoutputFile=${TARGET}/depTmp.txt -DoutputScope=true 
grep -v '|' ${TARGET}/depTmp.txt  | sed '1d' | cut -c 3- > ${TARGET}/dep.txt 

cat ${TARGET}/dep.txt | grep -v ':test' | sort > ${TARGET}/depProd.txt
cat ${TARGET}/dep.txt | grep  ':test' | sort > ${TARGET}/depTools.txt
rm -f ${TARGET}/dep.txt ${TARGET}/depTmp.txt

npm list --depth=0 --prod=true 2> /dev/null | sed '1d' | cut -c 4- > ${TARGET}/depNpmProd.txt
npm list --depth=0 --dev=true 2> /dev/null  | sed '1d' | cut -c 4- > ${TARGET}/depNpmTools.txt

npm audit --production --parseable > ${TARGET}/auditSecuAngular.txt

diff $SCRIPTDIR/references ${TARGET} > ${TARGET}/delta.txt
if [ "$?" != "0" ] ;
then
   echo "ERROR: Dependency are modify, verify: ${TARGET}/delta.txt"
   echo "  and update SPVP_ArchiveManager_FOSS-List_V1.0.0.xlsx"
   echo "*********"
   echo ""
   cat ${TARGET}/delta.txt
   exit 1
fi

echo "dependency information is in ${TARGET}"
echo "Audit secu of npm is in ${TARGET}/auditSecuAngular.txt"