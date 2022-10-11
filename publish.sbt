disablePlugins(aether.AetherPlugin)
enablePlugins(aether.SignedAetherPlugin)

overridePublishSignedSettings
overridePublishLocalSettings

publishTo := {
  if (isSnapshot.value) {
    Some(Opts.resolver.sonatypeSnapshots)
  } else {
    Some(Opts.resolver.sonatypeStaging)
  }
}

pomIncludeRepository := { x =>
  false
}

packageOptions += {
  val title  = name.value
  val ver    = version.value
  val vendor = organization.value

  Package.ManifestAttributes(
    "Created-By"               -> "Scala Build Tool",
    "Built-By"                 -> System.getProperty("user.name"),
    "Build-Jdk"                -> System.getProperty("java.version"),
    "Specification-Title"      -> title,
    "Specification-Version"    -> ver,
    "Specification-Vendor"     -> vendor,
    "Implementation-Title"     -> title,
    "Implementation-Version"   -> ver,
    "Implementation-Vendor-Id" -> vendor,
    "Implementation-Vendor"    -> vendor
  )
}

credentials ++= Seq(
  Credentials(Path.userHome / ".sbt" / "sonatype_credential"),
)

homepage := Some(url("http://constretto.org"))

startYear := Some(2017)

licenses := Seq(
  "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
)

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { _ =>
  false
}

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

scmInfo := Some(
  ScmInfo(
    new URL("http://github.com/constretto/constretto-scala"),
    "scm:git:git@github.com:constretto/constretto-scala.git",
    Some("scm:git:git@github.com:constretto/constretto-scala.git")
  ))

developers ++= List(
  Developer(
    "kaarenilsen",
    "Kaare Nilsen",
    "kaare.nilsen@gmail.com",
    new URL("http://twitter.com/kaarenilsen")
  )
)
