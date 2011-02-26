package org.constretto

import exception.ConstrettoExpressionException
import internal.provider.ConfigurationProvider
import internal.store.{IniFileConfigurationStore, PropertiesStore}
import model.Resource

object Constretto {
  def configuration(constrettoConfiguration: ConstrettoConfiguration): Constretto = new Constretto {
    def config = constrettoConfiguration
  }

  def apply(stores: Seq[ConfigurationStore], tags: String*): Constretto = {
    val withResources = stores.foldLeft(new ConfigurationProvider)(_.addConfigurationStore(_))
    val withTags = tags.foldLeft(withResources)(_.addTag(_))
    Constretto.configuration(withResources.getConfiguration)
  }

  def properties(props: String*): ConfigurationStore = props.map(new Resource(_)).foldLeft(new PropertiesStore)(_.addResource(_))

  def inis(i: String*): ConfigurationStore = i.map(new Resource(_)).foldLeft(new IniFileConfigurationStore)(_.addResource(_))

  def encryptedProperties(props: String*): ConfigurationStore = props.map(new Resource(_)).foldLeft(new PropertiesStore)(_.addResource(_))

}

trait Constretto {
  protected def config: ConstrettoConfiguration

  def get[T](name: String)(implicit converter: ScalaValueConverter[T]): Option[T] = {
    try {
      Some(converter.convert(config.evaluateToString(name)))
    } catch {
      case _: ConstrettoExpressionException => None
    }
  }

  def apply[T](name: String)(implicit converter: ScalaValueConverter[T]): T =
    converter.convert(config.evaluateToString(name))
}


