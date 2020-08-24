FROM osgeo/gdal:ubuntu-full-latest

ARG XMX=1024m
ARG PROFILE=production

ENV XMX=$XMX
ENV PROFILE=$PROFILE

VOLUME /tmp

ADD ./build/libs/st-microservice-ili-0.0.1-SNAPSHOT.jar st-microservice-ili.jar

RUN apt-get update && apt-get install -y openjdk-11-jdk

EXPOSE 8080

ENTRYPOINT java -Xmx$XMX -jar /st-microservice-ili.jar --spring.profiles.active=$PROFILE