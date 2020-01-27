#!/bin/sh
# Exit on error. Append "|| true" if you expect an error.
set -o errexit
# Exit on error inside any functions or subshells.
set -o errtrace
# Do not allow use of undefined vars. Use ${VAR:-} to use an undefined VAR
set -o nounset
# Catch the error in case mysqldump fails (but gzip succeeds) in `mysqldump |gzip`
set -o pipefail
# Turn on traces, useful while debugging but commented out by default
#curl set -o xtrace



echo "POSTGRES_HOST: ${POSTGRES_HOST:? Need POSTGRES_HOST value}"
echo "POSTGRES_PORT: ${POSTGRES_PORT:=5432}"
echo "POSTGRES_USER: ${POSTGRES_USER:=archivemanager}"
echo "POSTGRES_DATABASE: ${POSTGRES_DB:=archivemanager}"


# copy necessary file
# /home/archivemanager/config/tls/keystore.p12 don't existe take default file
if [ ! -f /home/archivemanager/config/tls/keystore.p12 ] ; then
  mkdir -p /home/archivemanager/config/tls
  cp /keystore.p12 /home/archivemanager/config/tls/keystore.p12
fi

JAVA_OPTS="-Dspring.datasource.password=${POSTGRES_PASSWORD:-} -Dspring.datasource.username=${POSTGRES_USER} -Dserver.ssl.key-store-password=${KEY_STORE_PASSWORD}"
# wait-for-postgres


until PGPASSWORD=${POSTGRES_PASSWORD:-} psql --host "${POSTGRES_HOST}" --port "${POSTGRES_PORT}" -U "${POSTGRES_USER}" -c '\q'; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up "

umask 002

# Start Application
echo "The application start now"
export SPRING_DATASOURCE_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /home/archivemanager/config/:/app/resources/:/app/classes/:/app/libs/* "com.airbus.archivemanager.ArchivemanagerApp"  "$@"
