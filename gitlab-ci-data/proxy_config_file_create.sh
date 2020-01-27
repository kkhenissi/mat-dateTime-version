if [ ! -z  "${PROXY}" ] ;then 
  if [[ ! $PROXY =~ "http://" ]] ; then
    echo "PROXY Variable must be http://uses:pass@host:port"
    exit 1
  fi
  PROXY_CMD=$(echo $PROXY | sed  s'|http://||')
  if [[ $PROXY =~ "@" ]] ; then
    USER_CMD=$(echo $PROXY_CMD |awk -F @ '{print $1}')
    HOST_CMD=$(echo $PROXY_CMD |awk -F @ '{print $2}')

    USER=$(echo $USER_CMD | awk -F : '{print $1}')
    PASS=$(echo $USER_CMD | awk -F : '{print $2}')
  else
    HOST_CMD=$PROXY_CMD
  fi
  HOST=$(echo $HOST_CMD | awk -F : '{print $1}')
  PORT=$(echo $HOST_CMD | awk -F : '{print $2}')

  echo "Create .mvn/jvm.config"
  mkdir -p .mvn
  echo "-Dhttp.proxyHost=$HOST" > .mvn/jvm.config
  echo "-Dhttp.proxyPort=$PORT" >> .mvn/jvm.config
  echo "-Dhttps.proxyHost=$HOST" >> .mvn/jvm.config
  echo "-Dhttps.proxyPort=$PORT" >> .mvn/jvm.config
  if [ ! -z "$USER" ] ;then
    echo "-Dhttp.proxyUser=$USER" >> .mvn/jvm.config
    echo "-Dhttp.proxyPassword=$PASS" >> .mvn/jvm.config
  fi
  if [ ! -z "$NO_PROXY" ] ;then
    export no_proxy=$NO_PROXY
    NO_PROXY_MVN=$(for h in $(echo $NO_PROXY | sed s'/,/ /g'); do echo -n "${h}|*${h}|";done)
    echo "-Dhttp.nonProxyHosts=${NO_PROXY_MVN%?}" >> .mvn/jvm.config
  fi

  echo "Create $HOME/.npmrc"
  echo "proxy=$PROXY" > $HOME/.npmrc
  echo "https-proxy=$PROXY" >> $HOME/.npmrc
  echo "https_proxy=$PROXY" >> $HOME/.npmrc
else
  echo "No PROXY is Configured"
fi
