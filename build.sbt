name := "distributed-cache-on-k8s-poc"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.9",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.9",
  "org.slf4j" % "slf4j-simple" % "1.7.25" % Test
)