import sbt._
import Keys._
object Settings {

  lazy val credentialsSetting = credentials ++=
    (Seq("SONATYPE_USER", "SONATYPE_PASSWORD").map(k => Option(System.getenv(k))) match {
      case Seq(Some(user), Some(pass)) =>
        Seq(Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass))
      case _ =>
        Seq.empty[Credentials]
    })

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.constretto",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.9.1",
    crossScalaVersions := Seq("2.8.0", "2.8.1", "2.9.0"),
    credentialsSetting,
    publishTo <<= (version) {
      version: String =>
        if (version.trim.endsWith("SNAPSHOT"))
          Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        else
          Some("Sonatype Nexus Repository Manager" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    }
  )
}

object Dependencies {
  val constrettoVersion = "2.0-beta-4"

  val constretto = "org.constretto" % "constretto-core" % constrettoVersion
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
          <name>
            {name}
          </name>,
          <description>
            {desc}
          </description>,
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
            <system>jira</system>
            <url>http://constretto.jira.com/browse/CS</url>
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
      libraryDependencies ++= Dependencies.deps
    )
  )
}

