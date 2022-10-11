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

import org.constretto.internal.converter._

import java.io.File
import java.net.{InetAddress, URI, URL}
import java.util.Locale

/**
  * @author jteigen
  * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
  */
object Converter {

  def apply[T](f: Json => T): Converter[T] = new Converter[T] {
    def convert(value: Json) = f(value)
  }

  private case class not(expected: String) {
    def list(l: List[Json]) = sys.error("Expected " + expected + ", got " + l)

    def map(o: JObject) = sys.error("Expected " + expected + ", got " + o)

    def string(s: String) = sys.error("Excpected " + expected + ", got " + s)
  }

  def fromString[T](f: String => T) = apply(_.fold(not("string").list, not("string").map, f))

  def fromObject[T](f: JObject => T) = apply(_.fold(not("object").list, f, not("object").string))

  def fromList[T](f: Json => T) = apply(_.fold(_.map(f), not("list").map, not("list").string))

  def fromConstretto[A, B](v: ValueConverter[A], f: A => B = identity[A] _) = fromString(p => f(v.fromString(p)))

  implicit def listConverter[T](implicit cv: Converter[T]): Converter[List[T]] = fromList(cv.convert)

  implicit def mapConverter[K, V](implicit keyConv: Converter[K], valueConv: Converter[V]): Converter[Map[K, V]] = fromObject {
    _.data.map {
      case (k, v) => (keyConv.convert(JPrimitive(k)), valueConv.convert(v))
    }
  }

  implicit val booleanConverter: Converter[Boolean] = fromConstretto[java.lang.Boolean, Boolean](new BooleanValueConverter, _.booleanValue)
  implicit val byteConverter: Converter[Byte] = fromConstretto[java.lang.Byte, Byte](new ByteValueConverter, _.byteValue)
  implicit val doubleConverter: Converter[Double] = fromConstretto[java.lang.Double, Double](new DoubleValueConverter, _.doubleValue)
  implicit val fileConverter: Converter[File] = fromConstretto[java.io.File, java.io.File](new FileValueConverter)
  implicit val floatConverter: Converter[Float] = fromConstretto[java.lang.Float, Float](new FloatValueConverter, _.floatValue)
  implicit val inetConverter: Converter[InetAddress] = fromConstretto[java.net.InetAddress, java.net.InetAddress](new InetAddressValueConverter)
  implicit val intConverter: Converter[Int] = fromConstretto[java.lang.Integer, Int](new IntegerValueConverter, _.intValue)
  implicit val localeConverter: Converter[Locale] = fromConstretto[java.util.Locale, java.util.Locale](new LocaleValueConverter)
  implicit val longConverter: Converter[Long] = fromConstretto[java.lang.Long, Long](new LongValueConverter, _.longValue)
  implicit val shortConverter: Converter[Short] = fromConstretto[java.lang.Short, Short](new ShortValueConverter, _.shortValue)
  implicit val stringConverter: Converter[String] = fromConstretto[String, String](new StringValueConverter)
  implicit val urlConverter: Converter[URL] = fromConstretto[java.net.URL, java.net.URL](new UrlValueConverter)
  implicit val uriConverter: Converter[URI] = fromConstretto[java.net.URI, java.net.URI](new UriValueConverter)

}

trait Converter[T] {
  def convert(value: Json): T
}
