FROM jhipster/jhipster:v6.3.1

RUN curl -f -o /home/jhipster/gatling.zip https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.0/gatling-charts-highcharts-bundle-3.3.0-bundle.zip && \
   unzip /home/jhipster/gatling.zip -d /home/jhipster/ && \
   mv /home/jhipster/gatling-charts-highcharts-bundle-3.3.0 /home/jhipster/gatling && \
   rm -f /home/jhipster/gatling.zip

RUN curl -f -L -o /home/jhipster/karate.jar https://dl.bintray.com/ptrthomas/karate/karate-0.9.5.RC5.jar