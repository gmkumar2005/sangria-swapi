name := "sangria-swapi"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  filters,
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test")

libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.1"
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.1"


routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
