resolvers ++= Seq(
	"Web plugin repo" at "http://siasia.github.com/maven2",
	"sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
	Resolver.url("Typesafe repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns)
)

libraryDependencies += "com.github.mpeltonen" %% "sbt-idea" % "0.10.0"

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+v))

