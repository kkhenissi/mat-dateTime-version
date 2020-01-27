
RESULT_FOLDER=target/test-results/gatling


export JAVA_OPTS="-DbaseURL=${TARGET_URL:-http://localhost:8080} -DstsPath=${STS_PATH:-/home/STS}"

mkdir -p /tmp/STS/gatlingTest
mkdir -p ${RESULT_FOLDER}
rm -rf ${RESULT_FOLDER}/index.html
touch ${RESULT_FOLDER}/index.html

for file in $(ls ./src/test/gatling//user-files/simulations/) ; do
  SIMULATION=${file%.scala}
  echo "Lauch gatling on ${SIMULATION}"
  /home/jhipster/gatling/bin/gatling.sh --simulations-folder ./src/test/gatling --results-folder ${RESULT_FOLDER} --simulation ${SIMULATION}
done

rm -rf ${RESULT_FOLDER}/index.html
echo "<html> <ul>" >> ${RESULT_FOLDER}/index.html
for file in $(ls ${RESULT_FOLDER}) ; do
  if [ "$file" != "index.html" ] ; then
    SIMULATION=$(echo $file | awk -F '-' '{print $1}')
    echo "<li> <a href=${file}/index.html> $SIMULATION </a> </li>" >> ${RESULT_FOLDER}/index.html
  fi
done
echo "</ul></html>" >> ${RESULT_FOLDER}/index.html

