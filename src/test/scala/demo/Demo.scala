package demo

import org.constretto._
import Constretto._

object Foo {
  implicit def fooConverter(implicit intC:ScalaValueConverter[Int]) = ScalaValueConverter[Foo](_.split(":") match {
    case Array(first, second) => Foo(first, intC.convert(second))
  })
}
case class Foo(a:String, b:Int)

object Demo {
  def main(args:Array[String]){

    val constretto = Constretto(List(properties("classpath:test.properties")))

    val existsString:Option[String] = constretto.get[String]("string")
    val existsInt:Option[Int] = constretto.get[Int]("int")
    val existsDouble:Option[Double] = constretto.get[Double]("double")

    val doesNotExist:Option[Int] = constretto.get[Int]("nah")

//    val unknownType:Option[Float] = constretto.get[Float]("urk")

    println(existsString)
    println(existsInt)
    println(existsDouble)
    println(doesNotExist)
//    println(unknownType)


    val foo:Option[Foo] = constretto.get[Foo]("foo")

    println(foo)

//    val foo1 = constretto[Foo]("lkasf")

//    println(foo1)

  }
}