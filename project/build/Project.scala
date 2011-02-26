import sbt._

class Project(info:ProjectInfo) extends DefaultProject(info) {
  val constrettoRepo = "constretto-repo" at "http://repo.constretto.org/content/repositories/snapshots"
  val contrettoCore = "org.constretto" % "constretto-core" % "2.0-SNAPSHOT"
}