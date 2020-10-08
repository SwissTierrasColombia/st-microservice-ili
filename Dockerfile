FROM osgeo/gdal:ubuntu-full-latest

ARG XMX=1024m
ARG PROFILE=production
ARG CLOUD_CONFIG

ENV XMX=$XMX
ENV PROFILE=$PROFILE
ENV CLOUD_CONFIG=$CLOUD_CONFIG

VOLUME /tmp

ADD  ./target/st-microservice-ili-1.7.4.jar st-microservice-ili.jar

RUN apt-get update && apt-get install -y openjdk-11-jdk

EXPOSE 8080

ENTRYPOINT java -Xmx$XMX -jar /st-microservice-ili.jar --spring.profiles.active=$PROFILE --spring.cloud.config.uri=$CLOUD_CONFIG