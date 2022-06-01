FROM openjdk:11

ARG XMX=1024m
ARG PROFILE=production
ARG CLOUD_CONFIG

ENV XMX=$XMX
ENV PROFILE=$PROFILE
ENV CLOUD_CONFIG=$CLOUD_CONFIG

VOLUME /tmp

ADD  ./target/st-microservice-ili-2.2.0.jar st-microservice-ili.jar

EXPOSE 8080

ENTRYPOINT java -Xmx$XMX -jar /st-microservice-ili.jar --spring.profiles.active=$PROFILE --spring.cloud.config.uri=$CLOUD_CONFIG