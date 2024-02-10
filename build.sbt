ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "University Grade"
  )
libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.4.0",
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.16",

  "de.heikoseeberger" %% "akka-http-json4s" % "1.37.0",
  // Дополнительные библиотеки JSON, если необходимо
  "org.json4s" %% "json4s-native" % "4.0.3",
  "org.json4s" %% "json4s-jackson" % "4.0.3",
  "com.typesafe.akka" %% "akka-actor" % "2.6.16"
)