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

import exception.ConstrettoException
import org.constretto.internal.converter._
import java.lang.reflect.Type
import com.google.gson._

/**
 * @author jteigen
 */
object ScalaValueConverter {

  sealed trait Json {
    def fold[A](arr:List[Json] => A, obj:Map[String, Json] => A, primitive:String => A) = this match {
      case JArray(data) => arr(data)
      case JObject(data) => obj(data)
      case JPrimitive(value) => primitive(value)
    }
  }
  case class JArray(data:List[Json]) extends Json
  case class JObject(data:Map[String, Json]) extends Json
  case class JPrimitive(value:String) extends Json

  private def nope(json:Any) = throw new ConstrettoException("you're an idiot")

  private[constretto] object gson {
    val builder = new GsonBuilder()
    builder.registerTypeAdapter(classOf[Json], new JsonDeserializer[Json] {

      import collection.JavaConverters._

      def handleArray(jsonArray: JsonArray): JArray = JArray(jsonArray.iterator().asScala.map(handle).toList)

      def handleObject(jsonObject: JsonObject): JObject = {
        JObject(jsonObject.entrySet().asScala.map {
          e =>
            val key = e.getKey
            val value = e.getValue
            key -> handle(value)
        }.toMap)
      }

      def handle(json: JsonElement): Json = {
        if (json.isJsonNull) null
        else if (json.isJsonPrimitive) JPrimitive(json.getAsString)
        else if (json.isJsonArray) handleArray(json.getAsJsonArray)
        else handleObject(json.getAsJsonObject)
      }

      def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) = handle(json)
    })

    def parse(s:String):Json = builder.create().fromJson(s, classOf[Json])
  }

  def apply[T](f: Json => T): ScalaValueConverter[T] = new ScalaValueConverter[T] {
    def convert(value: Json) = f(value)
  }

  def fromConstretto[A, B](v: ValueConverter[A], f: A => B = identity[A] _): ScalaValueConverter[B] = new ScalaValueConverter[B] {
    def convert(value: Json) = value.fold(nope, nope, p => f(v.fromString(p)))
  }

  implicit def listConverter[T](implicit cv: ScalaValueConverter[T]): ScalaValueConverter[List[T]] = new ScalaValueConverter[List[T]] {
    def convert(value: Json) = value.fold[List[T]](_.map(cv.convert), nope, nope)
  }

  implicit def mapConverter[K,V](implicit keyConv: ScalaValueConverter[K], valueConv: ScalaValueConverter[V]): ScalaValueConverter[Map[K,V]] = new ScalaValueConverter[Map[K,V]] {
    def asMap(map:Map[String, Json]) = map.map{ case (k, v) => (keyConv.convert(JPrimitive(k)), valueConv.convert(v))}
    def convert(value: Json) = value.fold(nope, asMap, nope)
  }

  implicit val booleanConverter = fromConstretto[java.lang.Boolean, Boolean](new BooleanValueConverter, _.booleanValue)
  implicit val byteConverter = fromConstretto[java.lang.Byte, Byte](new ByteValueConverter, _.byteValue)
  implicit val doubleConverter = fromConstretto[java.lang.Double, Double](new DoubleValueConverter, _.doubleValue)
  implicit val fileConverter = fromConstretto[java.io.File, java.io.File](new FileValueConverter)
  implicit val floatConverter = fromConstretto[java.lang.Float, Float](new FloatValueConverter, _.floatValue)
  implicit val inetConverter = fromConstretto[java.net.InetAddress, java.net.InetAddress](new InetAddressValueConverter)
  implicit val intConverter = fromConstretto[java.lang.Integer, Int](new IntegerValueConverter, _.intValue)
  implicit val localeConverter = fromConstretto[java.util.Locale, java.util.Locale](new LocaleValueConverter)
  implicit val longConverter = fromConstretto[java.lang.Long, Long](new LongValueConverter, _.longValue)
  implicit val shortConverter = fromConstretto[java.lang.Short, Short](new ShortValueConverter, _.shortValue)
  implicit val stringConverter = fromConstretto[String, String](new StringValueConverter)
  implicit val urlConverter = fromConstretto[java.net.URL, java.net.URL](new UrlValueConverter)
}

trait ScalaValueConverter[T] {
  def convert(value: ScalaValueConverter.Json): T
}