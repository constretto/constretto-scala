import sbt._
import sbt.Keys._
import scala.Some

object BuildSettings {
  val version = "1.1"
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
    scalaVersion := "2.10.3",
    crossScalaVersions := Seq("2.9.0", "2.9.0-1", "2.9.1", "2.9.2", "2.9.3", "2.10.4", "2.11.1"),
    credentialsSetting,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    shellPrompt := ShellPrompt.buildShellPrompt,
    publishTo <<= (version) {
      version: String =>
        if (version.trim.endsWith("SNAPSHOT"))
          Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        else
          Some("Sonatype Nexus Repository Manager" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    }
  )
}

object ShellPrompt {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  val current = """\*\s+([\w-]+)""".r

  def gitBranches = ("git branch --no-color" lines_! devnull mkString)

  val buildShellPrompt = {
    (state: State) => {
      val currBranch =
        current findFirstMatchIn gitBranches map (_ group (1)) getOrElse "-"
      val currProject = Project.extract(state).currentProject.id
      "%s:%s:%s> ".format(
        currProject, currBranch, BuildSettings.version
      )
    }
  }
}

object Dependencies {
  val constrettoVersion = "2.1.4"

  val constretto = "org.constretto" % "constretto-core" % constrettoVersion

  val scalatestVersions = Map("2.9" -> "1.9.2", "2.10" -> "2.1.7", "2.11" -> "2.1.7")

  def majorVersion(scalaVersion: String) = {
    """\d+\.\d+""".r findFirstIn scalaVersion getOrElse sys.error(s"Unknown scala version $scalaVersion")
  }

  def scalatestDependency(scalaVersion: String) = "org.scalatest" %% "scalatest" % scalatestVersions.getOrElse(majorVersion(scalaVersion), sys.error(s"Unknown scala version $scalaVersion")) % "test"

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
      pomExtra <<= (pomExtra, name, description) {
        (extra, name, desc) => extra ++ Seq(
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
        )
      },
      libraryDependencies <+= scalaVersion(Dependencies.scalatestDependency(_)),

      libraryDependencies ++= Dependencies.deps

   )
  )
}

