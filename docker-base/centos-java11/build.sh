#!/bin/sh

# Exit on error inside any functions or subshells.
set -o errtrace
# Catch the error in case mysqldump fails (but gzip succeeds) in `mysqldump |gzip`
set -o pipefail
# Turn on traces, useful while debugging but commented out by default
#set -o xtrace

if [ ! -z "$PROXY" ] ;then
   echo "Proxy is set"
   export BUILD_ARG="--build-arg https_proxy=${PROXY} --build-arg http_proxy=${PROXY}"
else
   echo "No Proxy configuration define PROXY env variable!"
fi

export BUILD_ARG="${BUILD_ARG} --build-arg cont_archivemanager_uid=${cont_archivemanager_uid:-5667}  --build-arg cont_spvp_gid=${cont_spvp_gid:-5667}"
echo  $BUILD_ARG

docker build $BUILD_ARG $* . 
