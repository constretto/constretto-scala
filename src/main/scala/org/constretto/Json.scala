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

/**
 * @author jteigen
 */
sealed trait Json {
  def fold[A](arr: List[Json] => A, obj: JObject => A, primitive: String => A) = this match {
    case JArray(data) => arr(data)
    case ject@JObject(data) => obj(ject)
    case JPrimitive(value) => primitive(value)
  }
}

case class JArray(data: List[Json]) extends Json

case class JObject(data: Map[String, Json]) extends Json {
  def apply[A](name: String)(implicit converter: Converter[A]) = converter.convert(data(name))
  def get[A](name: String)(implicit converter: Converter[A]) = data.get(name).map(converter.convert)
}

case class JPrimitive(value: String) extends Json