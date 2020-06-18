FROM osgeo/gdal:ubuntu-full-latest

VOLUME /tmp

ADD ./build/libs/st-microservice-ili-0.0.1-SNAPSHOT.jar st-microservice-ili.jar

RUN apt-get update && apt-get install -y openjdk-13-jdk

EXPOSE 8080

ENTRYPOINT java -jar /st-microservice-ili.jar