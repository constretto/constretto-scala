/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto

import exception.ConstrettoExpressionException
import internal.store.{IniFileConfigurationStore, PropertiesStore}
import model.Resource

/**
 * @author jteigen
 */
object Constretto {
  def configuration(constrettoConfiguration: ConstrettoConfiguration): Constretto = new Constretto {
    def config = constrettoConfiguration
  }

  def apply(stores: Seq[ConfigurationStore], tags: String*): Constretto = {
    val withResources = stores.foldLeft(new ConstrettoBuilder)(_.addConfigurationStore(_))
    val withTags = tags.foldLeft(withResources)(_.addCurrentTag(_))
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


