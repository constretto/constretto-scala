import sbt._
import sbt.Keys._
import scala.Some

object BuildSettings {
  val version = "1.2"
}

object Settings {

  lazy val credentialsSetting = credentials ++=
    (Seq("SONATYPE_USER", "SONATYPE_PASSWORD").map(k => Option(System.getenv(k))) match {
      case Seq(Some(user), Some(pass)) =>
        Seq(Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass))
      case _ =>
        Seq.empty[Credentials]
    })

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.constretto",
    version := BuildSettings.version,
    crossScalaVersions := Seq("2.12.1", "2.12.0", "2.11.7", "2.10.5"),
    scalaVersion := crossScalaVersions.value.head,
    credentialsSetting,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    publishTo := {
      if (isSnapshot.value)
        Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
      else
        Some("Sonatype Nexus Repository Manager" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    }
  )
}

object Dependencies {
  val constrettoVersion = "2.2.3"

  val constretto = "org.constretto" % "constretto-core" % constrettoVersion

  def majorVersion(scalaVersion: String) = {
    """\d+\.\d+""".r findFirstIn scalaVersion getOrElse sys.error(s"Unknown scala version $scalaVersion")
  }

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"

  val deps = Seq(constretto)
}

object ConstrettoBuild extends Build {

  import Settings._

  val description = SettingKey[String]("description")

  lazy val constrettoProject = Project(
    id = "constretto-scala",
    base = file("."),
    settings = buildSettings ++ Seq(
      description := "Constretto Scala API",
      pomExtra := Seq(
        <url>http://constretto.org</url>,
        <licenses>
          <license>
            <name>Apache</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>,
        <scm>
          <connection>scm:git:git@github.com:constretto/constretto-scala.git</connection>
          <developerConnection>scm:git:git@github.com:constretto/constretto-scala.git</developerConnection>
          <url>http://github.com/constretto/constretto-scala</url>
        </scm>,
        <issueManagement>
          <system>github</system>
          <url>https://github.com/constretto/constretto-scala/issues</url>
        </issueManagement>,
        <developers>
          <developer>
            <id>kaarenilsen</id>
            <name>Kaare Nilsen</name>
            <email>kaare.nilsen@gmail.com</email>
            <organization>Arktekk AS</organization>
            <organizationUrl>http://arktekk.no</organizationUrl>
            <roles>
              <role>Scala Developer</role>
            </roles>
            <timezone>+1</timezone>
          </developer>
        </developers>
      ),
      libraryDependencies += Dependencies.scalatest,
      libraryDependencies ++= Dependencies.deps
   )
  )
}
