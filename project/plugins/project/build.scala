import sbt._

object PluginDef extends Build {  
  override val projects = Seq(root)
  lazy val root = Project("plugins", file(".")) dependsOn (signerPlugin)
  lazy val signerPlugin = uri("git://github.com/rossabaker/sbt-signer-plugin")
}
