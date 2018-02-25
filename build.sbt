name := "distributed-cache-on-k8s-poc"

version := "1.0"

scalaVersion := "2.12.1"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.9",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.9",
  "org.slf4j" % "slf4j-simple" % "1.7.25" % Test,
  "com.spotify" % "docker-client" % "3.5.13"
)

maintainer in Docker := "rohitsharma9204@gmail.com"
dockerBaseImage := "openjdk:8"
dockerRepository := Some("localhost:5000")
dockerExposedPorts:= Seq(9000)
daemonUser in Docker := "root"