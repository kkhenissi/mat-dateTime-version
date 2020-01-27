#!/bin/bash
# Exit on error inside any functions or subshells.
set -o errtrace
# Do not allow use of undefined vars. Use ${VAR:-} to use an undefined VAR
set -o nounset
# Catch the error in case mysqldump fails (but gzip succeeds) in `mysqldump |gzip`
set -o pipefail
# Turn on traces, useful while debugging but commented out by default
set -o xtrace


# Get local script path et subfolder
SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

#Default Value
CREATE_CERTIFICATE=0
WITH_POSTGRES_PASS=1
TEST_STARTED=1
cd $SCRIPTPATH
export VOLUMES_ARCHIVEMANAGER_PATH=${VOLUMES_ARCHIVEMANAGER_PATH:-${SCRIPTPATH}/volumes}
export CONFIG_VOLUMES_ARCHIVEMANAGER_PATH=${CONFIG_VOLUMES_ARCHIVEMANAGER_PATH:-${VOLUMES_ARCHIVEMANAGER_PATH}/config}
export LOGS_VOLUMES_ARCHIVEMANAGER_PATH=${LOGS_VOLUMES_ARCHIVEMANAGER_PATH:-${VOLUMES_ARCHIVEMANAGER_PATH}/logs}
export LTS_VOLUMES_ARCHIVEMANAGER_PATH=${LTS_VOLUMES_ARCHIVEMANAGER_PATH:-${VOLUMES_ARCHIVEMANAGER_PATH}/LTS}
export STS_VOLUMES_ARCHIVEMANAGER_PATH=${STS_VOLUMES_ARCHIVEMANAGER_PATH:-${VOLUMES_ARCHIVEMANAGER_PATH}/STS}
export POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH=${POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH:-${VOLUMES_ARCHIVEMANAGER_PATH}/postgresql_data}
export GROUP_ARCHIVEMANAGER_ID=${GROUP_ARCHIVEMANAGER_ID:-5667}
export USER_ARCHIVEMANAGER_ID=${USER_ARCHIVEMANAGER_ID:-5667}
export POSTGRES_USER=${POSTGRES_USER:-archivemanager}
export POSTGRES_DB=${POSTGRES_USER:-archivemanager}
export TAG_ARCH_MGN=${CI_COMMIT_REF_SLUG}


DOCKER_COMPOSE=$SCRIPTPATH/docker-compose
COMPOSE_VERSION=$(docker-compose --version 2> /dev/null | awk -F '[ ,]' '{print $3}')
if [ "$?" == "0" ] ;
then
    if [[ "$COMPOSE_VERSION" > "1.16.0" ]] ;
    then
        DOCKER_COMPOSE=docker-compose
    fi
fi
 
#usage
usage() { echo -e "Usage: $0 [-h|--help] [-c|--cert-create] [-q|--without-postgres-pass] \n\
\t launch Archive Manager server and database\n\
\t -h|--help: this message\n\
\t -c|--cert-create: Create https certificate\n\
\t -q|--without-postgres-pass: no password for postgres authentification\n\
\t -w|--without-started-test: without wait for started process\n\
\t -p| --project-name NAME     Specify an alternate project name (default: directory name)\n\
Set KEY_STORE_PASSWORD env variable to the key store password if ${CONFIG_VOLUMES_ARCHIVEMANAGER_PATH}/tls/keystore.p12 exist.
"
}
#NOTE: This requires GNU getopt.  On Mac OS X and FreeBSD, you have to install this
# separately; see below.
opts=$(getopt --options hcqwp: --longoptions help,cert-create,without-postgres-pass,without-started-test,project-name: \
             --name "$(basename "$0")" -- "$@")
if [ $? != 0 ] ; then usage  >&2 ; exit 1 ; fi

# Note the quotes around `$opts': they are essential!
eval set -- "$opts"

while [[ $# -gt 0 ]]; do
    case "$1" in
        -h | --help ) usage; exit 0 ;;
        -c | --cert-create )  CREATE_CERTIFICATE=1; shift 1 ;;
        -q | --without-postgres-pass )  WITH_POSTGRES_PASS=0; shift 1 ;;
        -w | --without-started-test )  TEST_STARTED=0; shift 1 ;;
        -p | --project-name )  PROJECT_NAME=" --project-name $2"; shift 2 ;;
        -- ) shift; break ;;
        * ) break ;;
    esac
done

docker load -i ${SCRIPTPATH}/images.tar


if [ "${KEY_STORE_PASSWORD:-}" = "" ]; then
    
    nbTest=0
    echo ""
    echo -n "keystore password: "
    read -s KEY_STORE_PASSWORD1
    echo ""
    echo -n "Retype keystore password: "
    read -s KEY_STORE_PASSWORD2
    until [ "$KEY_STORE_PASSWORD1" = "$KEY_STORE_PASSWORD2" -o "$nbTest" = "3" ]; do
        echo ""
        echo "passwords differ"
        echo -n "keystore password: "
        read -s KEY_STORE_PASSWORD1
        echo ""
        echo -n "Retype keystore password: "
        read -s KEY_STORE_PASSWORD2
        nbTest=$(($nbTest + 1))
    done
    if [ "$nbTest" = "3" -o "${KEY_STORE_PASSWORD1:-}" = "" ]; then
        echo "ERROR: env KEY_STORE_PASSWORD! Aborting;"
        exit 1
    fi
    KEY_STORE_PASSWORD=${KEY_STORE_PASSWORD1}
    echo ""
fi

# Creation of https certificate
if [ "${CREATE_CERTIFICATE}" == "1" ]; then

    mkdir -p ${CONFIG_VOLUMES_ARCHIVEMANAGER_PATH}/tls/
    docker run --rm --user=$(id --user) -v ${CONFIG_VOLUMES_ARCHIVEMANAGER_PATH}/tls:/tls jhipster/jhipster:v6.3.1 keytool -genkey -alias archivemanager \
        -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore /tls/keystore.p12 -validity 365 -storepass "${KEY_STORE_PASSWORD}" \
        -dname "CN=admin archive manager, OU=ASW, O=AKKA, L=Toulouse, S=31, C=FR"
fi
 
 

# Password Postgres
if [ "${WITH_POSTGRES_PASS}" == "1" ]; then
    nbTest=0
    echo ""
    echo -n "postgresql password: "
    read -s POSTGRES_PASSWORD1
    echo ""
    echo -n "Retype postgresql password: "
    read -s POSTGRES_PASSWORD2
    until [ "$POSTGRES_PASSWORD1" = "$POSTGRES_PASSWORD2" -o "$nbTest" = "3" ]; do
        echo ""
        echo "passwords differ"
        echo -n "postgresql password: "
        read -s POSTGRES_PASSWORD1
        echo ""
        echo -n "Retype postgresql password: "
        read -s POSTGRES_PASSWORD2
        nbTest=$(($nbTest + 1))
    done
    if [ "$nbTest" = "3" -o "${POSTGRES_PASSWORD2:-}" = "" ]; then
        echo "ERROR: env POSTGRES_PASSWORD if necessary if ${CONFIG_VOLUMES_ARCHIVEMANAGER_PATH}/tls/keystore.p12 exist! Aborting;"
        exit 1
    fi
    echo 
    POSTGRES_PASSWORD=${POSTGRES_PASSWORD2}
fi

#Lauch compose
cd $SCRIPTPATH
export KEY_STORE_PASSWORD
export POSTGRES_PASSWORD
export POSTGRES_USER
export POSTGRES_DB
export COMPOSE_OPTIONS="\
   -e CONFIG_VOLUMES_ARCHIVEMANAGER_PATH \
   -e LOGS_VOLUMES_ARCHIVEMANAGER_PATH \
   -e LTS_VOLUMES_ARCHIVEMANAGER_PATH \
   -e STS_VOLUMES_ARCHIVEMANAGER_PATH \
   -e POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH \
   -e KEY_STORE_PASSWORD \
   -e POSTGRES_DB \
   -e POSTGRES_USER \
   -e POSTGRES_PASSWORD \
   -e USER_ARCHIVEMANAGER_ID \
   -e GROUP_ARCHIVEMANAGER_ID \
   -e TAG_ARCH_MGN"


${DOCKER_COMPOSE} ${PROJECT_NAME:-} down -v
${DOCKER_COMPOSE} ${PROJECT_NAME:-} up -d

if [ "${TEST_STARTED}" = "1" ] ;
then 

timeout 90s bash <<EOF
curl --insecure --silent --fail --output /dev/null https://127.0.0.1/management/health
while [ "\$?" != "0" ]; do
echo "waiting to start ..."
sleep 1
curl --insecure --silent --fail --output /dev/null https://127.0.0.1/management/health
done
EOF

    if [  "$?" = "0"  ]; then
    echo "Application is started"
    echo "To stop application:  './docker-compose stop' in $SCRIPTPATH directory"
    echo "to see log: './docker-compose logs' in $SCRIPTPATH directory"
    else 
    echo "ERROR: Application not started"
    ${DOCKER_COMPOSE} logs 
    echo "to see log: './docker-compose logs' in $SCRIPTPATH directory"
    fi
fi