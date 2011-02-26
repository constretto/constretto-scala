import sbt._

class Project(info:ProjectInfo) extends DefaultProject(info) {
  val constrettoRepo = "constretto-repo" at "http://repo.constretto.org/content/repositories/snapshots"
  val contrettoCore = "org.constretto" % "constretto-core" % "2.0-SNAPSHOT"

  override def managedStyle = ManagedStyle.Maven
  val publishTo = constrettoRepo

  Credentials(Path.userHome / ".ivy2" / "constretto.properties", log)
}