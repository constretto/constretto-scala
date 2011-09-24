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

import org.constretto._, Constretto._

/**
 * @author jteigen
 */
object Foo {
  implicit def fooConverter(implicit intConverter: Converter[Int], string:Converter[String]) = Converter.fromString[Foo]{
    _.split(":") match {
      case Array(first, second) => Foo(first, intConverter.convert(JPrimitive(second)))
    }
  }
}

case class Foo(a: String, b: Int)

object Demo extends App {
  val constretto = Constretto(List(properties("classpath:test.properties")))

  val existsString = constretto[String]("string")
  val existsInt: Option[Int] = constretto.get[Int]("int")
  val existsDouble: Option[Double] = constretto.get[Double]("double")
  val doesNotExist = constretto.get[Int]("nah")


  println(existsString)
  println(existsInt)
  println(existsDouble)
  println(doesNotExist)

  val foo: Option[Foo] = constretto.get[Foo]("foo")
  println(foo)

  val myFoos = constretto.get[List[Foo]]("myFoos")
  println(myFoos)

  val myMap = constretto.get[Map[Int,Float]]("myMap")
  println(myMap)

  val myNesting = constretto[List[Map[String, Foo]]]("wtf")
  println(myNesting)

  case class Address(postalCode: String, streetName:String)
  object Address {
    implicit def addressConverter(implicit string:Converter[String]) = Converter.fromMap{map =>
      Address(
        string.convert(map("postalCode")),
        string.convert(map("streetName")))
    }
  }
  case class Person(name:String, age:Int, address:Address)
  object Person{
    implicit def personConverter(implicit string:Converter[String], int:Converter[Int], address:Converter[Address]) = Converter.fromMap{ map =>
        Person(
          string.convert(map("name")),
          int.convert(map("age")),
          address.convert(map("address")))
    }
  }

  val myPerson = constretto[Person]("myPerson")
  println(myPerson)
}