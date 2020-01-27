#!/bin/bash

export URL=$1
TIMEOUT=$2
timeout $TIMEOUT bash << 'EOF'
curl --silent -f -o /dev/null ${URL}/management/health
while [ "$?" != "0" ]; do 
    sleep 1
    curl --silent -f -o /dev/null -f ${URL}/management/health
done
EOF

if [ "$?" = "0"  ]; then
   echo "server $URL is Up!"
else 
    echo "server $URL is Down!"
fi
