from docker:latest

ARG COMPOSE_VERSION=1.16.1

RUN apk add --update --no-cache \
    python \
    py-pip \
    curl \
    bash \
  && pip install --no-cache-dir docker-compose==$COMPOSE_VERSION \
  && rm -rf /var/cache/apk/*


WORKDIR /data
VOLUME /data