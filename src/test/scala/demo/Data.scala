package demo

import org.constretto.Converter
import java.net.URL

object Foo {
  implicit def fooConverter = Converter.fromString[Foo] {
    _.split(":") match {
      case Array(first, second) => Foo(first, second.toInt)
    }
  }
}

case class Foo(a: String, b: Int)


case class Address(postalCode: String, streetName: String)

object Address {
  implicit val addressConverter = Converter.fromObject {
    o =>
      Address(o[String]("postalCode"), o[String]("streetName"))
  }
}

case class Person(name: String, age: Int, address: Address)

object Person {
  implicit val personConverter = Converter.fromObject {
    o =>
      Person(o[String]("name"), o[Int]("age"), o[Address]("address"))
  }
}

case class Service(name: String, url: URL)

object Service {
  implicit val serviceConverter = Converter.fromObject {
    o =>
      Service(o[String]("name"), o[URL]("url"))
  }
}