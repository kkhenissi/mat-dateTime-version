# archivemanager

This application was generated using JHipster 6.3.1, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v6.3.1](https://www.jhipster.tech/documentation-archive/v6.3.1).

## Installation

### pre-requis

- docker v19.03.5 installer

### Commande

- copy archivemanager.tgz in installation directory (INSTAL_PATH)
- extract archivemanager.tgz `tar xvzf archivemanager.tgz`
- go to the new directory" `cd $INSTAL_PATH/archivemanager/`
- set following environement variables:

  - `CONFIG_VOLUMES_ARCHIVEMANAGER_PATH`: Host mount volume for Config directory, must contain files present in \$INSTAL_PATH/archivemanager/config, the keystore for hhtps is created in config/tls by default. see confg.application-prod.yml to change value. user archivemanager (uid=5667, gid=1000, 5006, 2001) must have a read/write access.
  - `LOGS_VOLUMES_ARCHIVEMANAGER_PATH`: Host mount volume for Logs directrories, user archivemanager (uid=5667, gid=1000, 5006, 2001) must have a read/write access.
  - `STS_VOLUMES_ARCHIVEMANAGER_PATH`: Host mount volume for STS root path, user archivemanager (uid=5667, gid=1000, 5006, 2001) must have a read/write access.
  - `LTS_VOLUMES_ARCHIVEMANAGER_PATH`: Host mount volume for LTS root path, user archivemanager (uid=5667, gid=1000, 5006, 2001) must have a read/write access.
  - `POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH`: Host mount volume for data base directory
  - `USER_ARCHIVEMANAGER_ID`: User id of archivemanager user 5667 by default
  - `GROUP_ARCHIVEMANAGER_ID`: Group id of archivemanager user 5667 by default

  #### Command for AKKA validation plateform (adminprod@and-ads-am-02.akka.eu)

  ```

  # In All Case:
  #---------------
  cd /home/adminprod/ArchiveManager
  tar xvzf archivemanager.tgz

  # If need to restart in blank or first time
  #---------------------------------------------

  cd /home/adminprod/ArchiveManager/archivemanager
  ./docker-compose down
  chown -R :5667 volumes/config
  chmod g+w volumes/config
  mkdir -p volumes/logs
  chown -R :5667 volumes/logs
  chmod g+w volumes/logs
  rm -rf volumes/config/tls
  rm -rf volumes/logs/*
  sudo rm -rf volumes/postgresql_data
  sudo rm -rf /data1/LTS/*

  # In Aall cases:
  #---------------
  cd /home/adminprod/ArchiveManager/archivemanager
  export CONFIG_VOLUMES_ARCHIVEMANAGER_PATH=$(pwd)/volumes/config
  export LOGS_VOLUMES_ARCHIVEMANAGER_PATH=$(pwd)/volumes/logs
  export POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH=$(pwd)/volumes/postgresql_data/
  export LTS_VOLUMES_ARCHIVEMANAGER_PATH=/data1/LTS
  export STS_VOLUMES_ARCHIVEMANAGER_PATH=/data2/STS
  export GROUP_ARCHIVEMANAGER_ID=5667
  ```

#### Command for ADS DERISK plateform (192.168.1.4)

    ```
    # if /data/work_area/gsc/archivemanagerVx.y.z exist
    cd /data/work_area/gsc/archivemanagerVx.y.z; ./docker-compose down
    cd ..
    rm -rf /data/work_area/gsc/archivemanagerVx.y.z

    # archivemanager.tgz must be in /data/work_area/gsc
    cd /data/work_area/gsc/
    tar xvzf archivemanager.tgz
    mv archivemanager archivemanagerV1.0.0
    cd /data/work_area/gsc/archivemanagerV1.0.0
    mv volumes share
    mkdir -p share/logs
    mkdir -p share/STS
    mkdir -p share/LTS
    chown -R :1001 share
    export CONFIG_VOLUMES_ARCHIVEMANAGER_PATH=/data/work_area/gsc/archivemanagerV1.0.0/share/config
    export LOGS_VOLUMES_ARCHIVEMANAGER_PATH=/data/work_area/gsc/archivemanagerV1.0.0/share/logs
    export POSTGRES_VOLUMES_ARCHIVEMANAGER_PATH=/data/work_area/gsc/archivemanagerV1.0.0/share/postgresql_data/
    export LTS_VOLUMES_ARCHIVEMANAGER_PATH=/data/work_area/gsc/archivemanagerV1.0.0/share/LTS
    export STS_VOLUMES_ARCHIVEMANAGER_PATH=/data/work_area/gsc/archivemanagerV1.0.0/share/STS
    export GROUP_ARCHIVEMANAGER_ID=1001
    ```

- First start with https certificat creation (config/tls/keystore.p12)  
  Passwords for certificate keystore and postgres connection will be requested

```bash
    ./launch.sh --cert-create
```

**Be carrefull** Remember passwords entered

- Second start with the same https certificate

```bash
    ./launch.sh
```

### Installation verification

- "Application is started" message is displayed
- the commande `curl --fail --insecure https://ServerName/management/health` return no error

### Post Installation

#### After a first installation

Pour des raison évidente de sécurité:

- Start the client (browsed to https://ServerName/ )
- To conect with admin/admin (Account/Sign in)
- Change admin password (Account/Password)
- Delete users: "user" and "system"

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    npm install

We use npm scripts and [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    npm start

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    npm install --save --save-exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    npm install --save-dev --save-exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/css/vendor.css](src/main/webapp/content/css/vendor.css) file:

```
@import '~leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts

## Building for production

### Packaging as jar

To build the final jar and optimize the archivemanager application for production, run:

    ./mvnw -Pprod clean verify

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar target/*.jar

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./mvnw -Pprod,war clean verify

## Testing

To launch your application's tests, run:

    ./mvnw verify

### Client tests

Unit tests are run by [Jest][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    npm test

For more information, refer to the [Running tests page][].

### Performance Test with [Gatling](http://gatling.io/)

Performance tests are done with Gatling, and are located in the src/test/gatling folder.
They are one test for each entity, and allows to test each of them with a lot of concurrent user requests.

To run Gatling tests, you must first install Gatling: please go to the [Gatling download page](https://gatling.io/open-source/start-testing/) and follow the instructions there.

- download: https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.0/gatling-charts-highcharts-bundle-3.3.0-bundle.zip
- unzip gatling-charts-highcharts-bundle-3.3.0-bundle.zip in `C:\Developpement\Programme` (by exemple, you can choose another target, but you must have rigth to write in this target without be an administrator)
- Add environement variable `GATLING_HOME` :`C:\Program Files (x86)\gatling-charts-highcharts-bundle-3.3.0`
- Add to environement variable `Path` `%GATLING_HOME%\bin
- In you soruces folder, launch the comande: `gatling.bat --simulations-folder ./src/test/gatling/user-files/simulations/ --results-folder ./src/test/gatling/target` and choose the simulation and select run sescriptio, you can type on _Retun_ key

_Note: Please note we do not allow to run Gatling from Maven or Gradle, as it causes some classpath issues with other plugins (mainly because of the use of Scala)._

### Test de validation Karate

Les tests automatique de validation sont génré avec Karate: https://github.com/intuit/karate

### options

-Env: Dans les commande suivante, modifier _val_ dans `-Dkarate.env=val` ou `--env=val` par la cible souhaité:

- dev: pour l'environnement de developpement (cible http://localhost:8080)
- val: pour l'environnement de validation AKKA (cible https://and-ads-am-01.akka.eu)
- derisk: pour l'environnement de validation Airbus (cible https://derisk)
- Admin password ajouter l 'option à la jre `-DarchvMgn.admin.password="adminPassword"` où adminPassword est le password du compte admin, par défaut, le mot de pass est 'admin'
- Url du serveur: ajouter l 'option à la jre `-DarchvMgn.baseUrl="https://host:port/"` par défaut, l'URL est positionné via l'environement

#### Suivant le besoin, les possibilité

- **Utilisation classique avec maven** (windows ou linux)
  - Acces à internet sans proxy
  - Jihipster environement doit être installé
    - Install Java 11.
    - Install Node.js from the Node.js website (please use an LTS 64-bit version, non-LTS versions are not supported)
    - Install JHipster: npm install -g generator-jhipster
  - Un serveur doit être démarré

```bash
./mvnw -Dtest=KarateTest -DargLine="-Dkarate.env=val" test
```

- Avec **Docker** et accès à internet sans proxy
  - Docker machine instalé
  - Ce positionner à la racine du clone du projet `git clone https://gitlab.akka.eu/ads-tesun/egnos/archivemanagermonoapp.git`
  - Un serveur doit être demarrer

```bash
docker run --rm -v ${PWD}:/home/jhipster/app -v ${PWD}/.m2:/home/jhipster/.m2  jhipster/jhipster ./mvnw -Dtest=KarateTest test -DargLine="-Dkarate.env=val"
```

- En mode **standalone**
  - JRE 11 installé
  - récupération du jar: karate.jar lien: https://dl.bintray.com/ptrthomas/karate/:karate-0.9.5.RC5.jar
    par exemple: `curl -f -L -o $HOME/karate.jar https://dl.bintray.com/ptrthomas/karate/karate-0.9.5.RC5.jar`
  - génération ou récupération du archivemanager-\*-tests.jar
  - se positionner dans src/test/java

```bash
export INSTALL_APP=/c/Developpement/workspace/archivemanagermonoapp
cd $INSTALL_APP/src/test/java
java -cp "$HOME/karate.jar:$INSTALL_APP/target/archivemanager-1.1.0-SNAPSHOT-tests.jar" \
  com.intuit.karate.Main  \
  --tags=~@ignore --env=dev  \
  ./com/airbus/archivemanager/karate/scenarii/senario/
```

- Avec **Docker** et seulement le **tar de livraison**
  - Sur la machine de deploiement
  - Se positionner dans le répertoire de deploiement
  - Un serveur doit être demarrer(résultat de tar xvvf archivemanager.tgz)

```bash
docker run --rm \
   -v ${PWD}/testData:/home/jhipster/app \
   -v ${LTS_VOLUMES_ARCHIVEMANAGER_PATH}:/home/LTS \
   -v ${STS_VOLUMES_ARCHIVEMANAGER_PATH}:/home/STS \
   -w /home/jhipster/app/src/test/java \
   arch-mgn-test:1.0 \
   java \
       -cp "/home/jhipster/karate.jar:/home/jhipster/app/target/archivemanager-1.1.0-tests.jar" \
       -DarchvMgn.admin.password="admin" \
       -DarchvMgn.baseUrl="https://$(hostname)/" \
       com.intuit.karate.Main \
       --tags=~@ignore \
       --env=derisk  \
       ./com/airbus/archivemanager/karate/scenarii/senario/

```

## Code quality

Sonar is used to analyse code quality.

In AKKA, the sonar server is: http://and-adsgeo-cis-04.akka.eu:9021  
 You can the result of last analyse on http://and-adsgeo-cis-04.akka.eu:9021/dashboard?id=archivemanager

You can start a local Sonar server (accessible on http://localhost:9021) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

or

For more information, refer to the [Code quality page][].

## Using Docker to simplify development

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw -Pprod verify jib:dockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration

The CI of Archive Manager is available on gitlat.  
The configuration file is .gitlab-ci.yml  
The CI Projet has 9 stages:

- **pre-build**: This stage must launch manualy, it create all docker base images. These images are push on gitlab docker registry.
- **build**: build java file
- **test**: Launch backend and front test
- **analyze**: Launch sonar analyse. see SONAR_URL and SONAR_TOKEN variable
- **package**: Generate the jar target
- **doc**: generate all code documentation
- **release**: generate docker image target and push it on gitlab registry docker
- **deploy**: deploy on target: see DEPLOY_TARGET_HOST variable
- **perf**: launch performance test on deploy instance

### CI Variables

Gitlab ci variables are define in https://gitlab.akka.eu/ads-tesun/egnos/archivemanagermonoapp/-/settings/ci_cd

#### Network variables

- **PROXY** : corporate proxy, The value must be: `http://host:port` or `http://username:password@host:port`, all non standart caracteres must be encode with %(hexa code)
- **NO_PROXY** : list of hosts or domaine that should not be joined through the proxy, with '**,**' as separator: exemple: `.akka.eu,localhost,127.0.0.1`
  _Note_: If PROXY is not define, no corporate proxy are use.

#### Build Variable

- **cont_archivemanager_uid** : uid of archivemanager docker image user to give write acces on Long Term Storage
- **cont_spvp_gid** : gid of archivemanager docker image group to give write acces on Short Term Storage, the simin(gid=2001) group is already created in archivemanager image

#### Quality variables

- **SONAR_URL**: sonar server URL
- **SONAR_TOKEN**: sonar access token
  _Note_: If SONAR_URL is not define, no SONAR analyse is reached.

#### Deploy variables

- **DEPLOY_TARGET_HOST**:

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 6.3.1 archive]: https://www.jhipster.tech/documentation-archive/v6.3.1
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v6.3.1/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v6.3.1/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v6.3.1/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v6.3.1/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v6.3.1/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v6.3.1/setting-up-ci/
[node.js]: https://nodejs.org/
[yarn]: https://yarnpkg.org/
[webpack]: https://webpack.github.io/
[angular cli]: https://cli.angular.io/
[browsersync]: https://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[jasmine]: https://jasmine.github.io/2.0/introduction.html
[protractor]: https://angular.github.io/protractor/
[leaflet]: https://leafletjs.com/
[definitelytyped]: https://definitelytyped.org/
