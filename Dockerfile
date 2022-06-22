FROM openjdk:11

ARG XMX=4096m
ARG PROFILE=production
ARG CLOUD_CONFIG
ARG ILI_QUEUE
ARG NEW_RELIC_ENVIRONMENT

ENV XMX=$XMX
ENV PROFILE=$PROFILE
ENV CLOUD_CONFIG=$CLOUD_CONFIG
ENV ILI_QUEUE=$ILI_QUEUE
ENV NEW_RELIC_ENVIRONMENT=$NEW_RELIC_ENVIRONMENT

VOLUME /tmp

ADD ./target/st-microservice-ili-2.4.2.jar st-microservice-ili.jar
ADD ./target/newrelic.jar newrelic.jar
ADD ./newrelic.yml newrelic.yml

EXPOSE 8080

ENTRYPOINT java -javaagent:/newrelic.jar -Dnewrelic.environment=$NEW_RELIC_ENVIRONMENT -Xmx$XMX -jar /st-microservice-ili.jar --spring.profiles.active=$PROFILE --spring.cloud.config.uri=$CLOUD_CONFIG --st.rabbitmq.queueInstance=$ILI_QUEUE