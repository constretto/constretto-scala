organization := "org.constretto"
name := "constretto-scala"
version := "1.3-SNAPSHOT"

val scala3Version = "3.2.0"
val scala2Version = "2.13.9"

val supportedScalaVersions = List(scala3Version, scala2Version)

scalaVersion := scala3Version

crossScalaVersions := supportedScalaVersions
releaseCrossBuild:= true

libraryDependencies ++= Seq(
  "org.constretto" % "constretto-core" % "2.2.3",
  "org.scalatest"  %% "scalatest"      % "3.2.13" % Test
)
