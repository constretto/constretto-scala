import sbt._
import sbt.Keys._
import com.rossabaker.sbt.signer.SignerPlugin
import SignerPlugin.Keys._

object SignerPluginConfig extends Plugin {
  override lazy val settings = Seq(
    signatureGenerator := Some(SignerPlugin.OpenPgpSignatureGenerator(
      name = "sbt-pgp", 
      password = System.getenv("SIGNER_PWD")))) ++ SignerPlugin.signerSettings
}
