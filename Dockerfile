FROM openjdk:8

ENV SBT_VERSION 1.1.0

RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

RUN sbt publishLocal

COPY /target/scala-2.12/distributed-cache-assembly-1.0.jar /app/distributed-cache-assembly-1.0.jar

WORKDIR /app
EXPOSE 9000
ENTRYPOINT java -jar distributed-cache-assembly-1.0.jar