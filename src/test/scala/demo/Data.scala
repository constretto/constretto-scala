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
package demo

import org.constretto.Converter
import java.net.URL


object Foo {
  implicit def fooConverter: Converter[Foo] = Converter.fromString[Foo] {
    _.split(":") match {
      case Array(first, second) => Foo(first, second.toInt)
    }
  }
}

case class Foo(a: String, b: Int)


case class Address(postalCode: String, streetName: String)

object Address {
  implicit val addressConverter: Converter[Address] = Converter.fromObject {
    o =>
      Address(o[String]("postalCode"), o[String]("streetName"))
  }
}

case class Person(name: String, age: Int, occupation:Option[String], address: Address)

object Person {
  implicit val personConverter: Converter[Person] = Converter.fromObject {
    o =>
      Person(o[String]("name"), o[Int]("age"), o.get[String]("occupation"), o[Address]("address"))
  }
}

case class Service(name: String, url: URL)

object Service {
  implicit val serviceConverter: Converter[Service] = Converter.fromObject {
    o =>
      Service(o[String]("name"), o[URL]("url"))
  }
}