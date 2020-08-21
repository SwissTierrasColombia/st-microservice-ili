FROM openjdk:12

ARG XMX=1024m

ENV XMX=$XMX

VOLUME /tmp

ADD ./build/libs/st-microservice-ili-0.0.1-SNAPSHOT.jar st-microservice-ili.jar

EXPOSE 9005

ENTRYPOINT java -Xmx$XMX -jar /st-microservice-ili.jar