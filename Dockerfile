FROM openjdk:12

VOLUME /tmp

ADD ./build/libs/st-microservice-ili-0.0.1-SNAPSHOT.jar st-microservice-ili.jar

EXPOSE 8080

ENTRYPOINT java -jar /st-microservice-ili.jar