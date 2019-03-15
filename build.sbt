name := "sangria-swapi"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"
val circeVersion = "0.10.0"
scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  filters,
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4")

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % "test"
) 
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.1"
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.1"
libraryDependencies += "org.sangria-graphql" %% "sangria-circe" % "1.2.1"
libraryDependencies += "io.circe" %% "circe-optics" % circeVersion

libraryDependencies += "com.dripower" %% "play-circe" % "2711.0"
libraryDependencies += "com.stephenn" %% "scalatest-json-jsonassert" % "0.0.3"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics",
).map(_ % circeVersion)


routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
