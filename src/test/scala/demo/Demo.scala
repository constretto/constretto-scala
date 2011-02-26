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

import org.constretto._
import Constretto._

/**
 * @author jteigen
 */
object Foo {
  implicit def fooConverter(implicit intConverter: ScalaValueConverter[Int]) = ScalaValueConverter[Foo](_.split(":") match {
    case Array(first, second) => Foo(first, intConverter.convert(second))
  })
}

case class Foo(a: String, b: Int)

object Demo {
  def main(args: Array[String]) {

    val constretto = Constretto(List(properties("classpath:test.properties")))

    val existsString: Option[String] = constretto.get[String]("string")
    val existsInt: Option[Int] = constretto.get[Int]("int")
    val existsDouble: Option[Double] = constretto.get[Double]("double")

    val doesNotExist: Option[Int] = constretto.get[Int]("nah")

    println(existsString)
    println(existsInt)
    println(existsDouble)
    println(doesNotExist)

    val foo: Option[Foo] = constretto.get[Foo]("foo")

    println(foo)
  }
}