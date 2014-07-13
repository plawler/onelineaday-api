name := """onelineaday-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.3"
)
