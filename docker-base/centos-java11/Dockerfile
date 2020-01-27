FROM gitlab.akka.eu:4567/ads-tesun/egnos/archivemanagermonoapp/i3s-osxd-c-ev3:7.7

RUN yum -y install java-11-openjdk postgresql  &&\
    yum -y clean all && \
    rm -rf /var/cache/yum && \
    find /tmp /var/tmp /var/log -mindepth 1 -delete -print

RUN keytool -genkey -alias archivemanager -storetype PKCS12 -keyalg RSA -keysize 2048 \
     -keystore /keystore.p12 -validity 365 -storepass password \
     -dname "CN=admin archive manager, OU=ASW, O=AKKA, L=Toulouse, S=31, C=FR"

ARG cont_spvp_gid=5668
ARG cont_archivemanager_uid=5667

RUN groupadd --gid $cont_spvp_gid  spvp && \
    groupadd --gid 2001 simin && \
    useradd -u $cont_archivemanager_uid -g users -G spvp,simin archivemanager

USER archivemanager

RUN mkdir -p /home/archivemanager/bin /home/archivemanager/config/archivemanager/tls /home/archivemanager/cots /home/archivemanager/logs /home/archivemanager/srcipts && ln -s /app/libs /home/archivemanager/lib 

COPY entrypoint.sh /home/archivemanager/srcipts/ 

USER root

RUN chmod a+x /home/archivemanager/srcipts/entrypoint.sh

USER archivemanager
