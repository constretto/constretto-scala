ThisBuild / organization := "org.constretto"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / crossScalaVersions := Seq("2.12.8", "2.13.1")
ThisBuild / name := "constretto-scala"
ThisBuild / version := "1.3-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.constretto" % "constretto-core" % "2.2.3",
  "org.scalatest"  %% "scalatest"      % "3.0.8" % Test
)
