FROM openjdk:8-jdk-alpine

RUN apk add --update curl && rm -rf /var/cache/apk/*

VOLUME /tmp
ADD target/mad-stats-1.0.0.jar app.jar
ENV JAVA_OPTS=""

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
