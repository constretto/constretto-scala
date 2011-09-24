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

/**
 * @author jteigen
 */
object Converter {

  def apply[T](f: Json => T): Converter[T] = new Converter[T] {
    def convert(value: Json) = f(value)
  }

  private case class not(expected:String) {
    def list(l:List[Json]) = sys.error("Expected "+expected+", got " + l)
    def map(o:Map[String, Json]) = sys.error("Expected "+expected+", got " + o)
    def string(s:String) = sys.error("Excpected " + expected+", got " + s)
  }

  def fromString[T](f:String => T) = apply(_.fold(not("string").list, not("string").map, f))
  def fromMap[T](f:Map[String, Json] => T) = apply(_.fold(not("map").list, f, not("map").string))
  def fromList[T](f:Json => T) = apply(_.fold(_.map(f), not("list").map, not("list").string))
  def fromConstretto[A, B](v: ValueConverter[A], f: A => B = identity[A] _) = fromString(p => f(v.fromString(p)))

  implicit def listConverter[T](implicit cv: Converter[T]) = fromList(cv.convert)

  implicit def mapConverter[K,V](implicit keyConv: Converter[K], valueConv: Converter[V]) = fromMap{
    _.map{ case (k, v) => (keyConv.convert(JPrimitive(k)), valueConv.convert(v))}
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

trait Converter[T] {
  def convert(value: Json): T
}