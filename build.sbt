name := "constretto-scala"

version := "1.0-SNAPSHOT" 

organization := "org.constretto"

scalaVersion := "2.9.0"

seq(webSettings :_*)

libraryDependencies ++= {
	Seq(
		"org.constretto" % "constretto-core" % "2.0-beta-4"
	)
}
