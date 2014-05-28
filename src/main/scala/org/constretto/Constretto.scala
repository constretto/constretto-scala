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
import org.constretto.internal.store.{EncryptedPropertiesStore, IniFileConfigurationStore, JsonStore, PropertiesStore}
import model.Resource
import scala.collection.JavaConverters._

/**
 * @author jteigen
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
object Constretto {
  def configuration(constrettoConfiguration: ConstrettoConfiguration): Constretto = new Constretto {
    def config = constrettoConfiguration
  }

  def apply(stores: Seq[ConfigurationStore], tags: String*): Constretto = {
    val withResources = stores.foldLeft(new ConstrettoBuilder)((acc, store) => acc.addConfigurationStore(store))
    val withTags = tags.foldLeft(withResources)((acc, tag) => acc.addCurrentTag(tag))
    Constretto.configuration(withTags.getConfiguration)
  }

  def properties(props: String*): ConfigurationStore = props.map(Resource.create(_)).foldLeft(new PropertiesStore)(_.addResource(_))

  def json(path: String, key: String, tag: Option[String] = None): ConfigurationStore = {
    tag.map(t => new JsonStore().addResource(Resource.create(path), key, t)).getOrElse(new JsonStore().addResource(Resource.create(path), key))

  }

  def inis(i: String*): ConfigurationStore = i.map(Resource.create(_)).foldLeft(new IniFileConfigurationStore)(_.addResource(_))

  def encryptedProperties(passwordProperty: String, props: String*): ConfigurationStore =
    props.map(Resource.create(_)).foldLeft(new EncryptedPropertiesStore(passwordProperty): PropertiesStore)(_.addResource(_))
}

trait Constretto {
  def config: ConstrettoConfiguration

  /**
   * Looks up an optional key in the configuration.
   *
   * @param name   The key to look up
   * @return The converted value for the expression, or None if expression not found, or conversion error occured.
   * @since 1.0
   */
  def get[T](name: String)(implicit converter: Converter[T]): Option[T] = {
    try {
      val parsedOpt = Option(CValueParser.parse(config.evaluate(name)))
      parsedOpt.flatMap(p => Option(converter.convert(p)))
    } catch {
      case _: ConstrettoExpressionException => None
    }
  }

  /**
   * Looks up an required key in the configuration.
   *
   * @param name   The key to look up
   * @return The converted value for the expression.
   * @throws ConstrettoExpressionException If the key is not found
   * @throws ConstrettoConversionException If a valid converter is not found for the target Type
   * @since 1.0
   */
  def apply[T](name: String)(implicit converter: Converter[T]): T = converter.convert(CValueParser.parse(config.evaluate(name)))

  /**
   * Provides all configuration key/value pairs in the current Constretto configuration
   *
   * @return Iterator of Pairs of configuration key, and values as strings
   * @since 1.1
   */
  def properties: Iterator[(String, String)] = config.iterator().asScala.map(p => (p.getKey, p.getValue))


  /**
   * Prepends (higher precedence) a new configuration tag at runtime.
   * Will try to reconfigure any classes configured with on() or at()
   *
   * @param tags the new Tag to be prepended to the list of constretto configuration tags.
   * @throws ConstrettoConversionException If a conversion error occurs when reconfiguring objects.
   * @since 1.1
   */
  @deprecated("Will be removed once Constretto 3 is released")
  def prependTag(tags: String*) {
    config.prependTag(tags: _*)
  }

  /**
   * Appends (lower precedence) a new configuration tag at runtime.
   * Will try to reconfigure any classes configured with on() or at()
   *
   * @param tags the new Tag to be appended to the list of constretto configuration tags.
   * @throws ConstrettoConversionException If a conversion error occurs when reconfiguring objects.
   * @since 1.1
   */
  @deprecated("Will be removed once Constretto 3 is released")
  def appendTag(tags: String*) {
    config.appendTag(tags: _*)
  }

  /**
   * Removes a configuration tag at runtime.
   * Will try to reconfigure any classes configured with on() or at()
   *
   * @param tags the new tags to be appended to the list of constretto configuration tags.
   * @throws ConstrettoConversionException If a conversion error occurs when reconfiguring objects.
   * @since 1.1
   */
  @deprecated("Will be removed once Constretto 3 is released")
  def removeTag(tags: String*) {
    config.removeTag(tags: _*)
  }

  /**
   * Resets all tags in Constretto to the ones originally
   * configured either with a ConfigurationContextResolver, or
   * by the ConstrettoBuilder class.
   *
   * @param reconfigure if set constretto will run the reconfigure() method after the reset.
   *                    Note this may result in exceptions from constretto if default values does not exist for all keys
   *                    injected in methods or fields annotated with @Configure or @Configuration
   * @throws ConstrettoConversionException If a conversion error occurs when reconfiguring objects.
   * @since 1.1
   */
  @deprecated("Will be removed once Constretto 3 is released")
  def resetTags(reconfigure: Boolean = false) {
    config.resetTags(reconfigure)
  }

  /**
   * Clears all tags in Constretto including the ones originally
   * configured either with a ConfigurationContextResolver, or
   * by the ConstrettoBuilder class. Resulting in Constretto having
   * no configuration tags registered.
   * <p/>
   * This is a non recoverable operation and after use you will need to build your tags from scratch.
   *
   * @param reconfigure if set constretto will run the reconfigure() method after clearing.
   *                    Note this may result in exceptions from constretto if default values does not exist for all keys
   *                    injected in methods or fields annotated with @Configure or @Configuration
   * @throws ConstrettoConversionException If a conversion error occurs when reconfiguring objects.
   * @since 1.1
   */
  @deprecated("Will be removed once Constretto 3 is released")
  def clearTags(reconfigure: Boolean = false) {
    config.clearTags(reconfigure)
  }

  /**
   * Gives a list over all the tags currently in use.
   *
   * @return current tags
   * @since 1.1
   */
  def getCurrentTags: Seq[String] = config.getCurrentTags.asScala.toSeq


}


