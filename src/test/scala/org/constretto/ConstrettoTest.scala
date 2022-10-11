package org.constretto

import org.scalatest._
import org.constretto.Constretto._
import org.constretto.exception.ConstrettoExpressionException
import demo.{Address, Person}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 *
 * @author zapodot at gmail dot com
 */
class ConstrettoTest extends AnyFunSuite with Matchers {

  test("An empty Constretto configuration should have only system properties and environment variables") {
    val constretto = Constretto(Nil)

    assert(constretto.properties.length === (System.getProperties.size() + System.getenv().size()))
  }

  test("A Constretto configuration initialized with a property file should contain all system properties, environment variables and all properties set in the file") {

    val constretto = Constretto(properties("classpath:test.properties") :: Nil, "test")

    assert(constretto[String]("baseUrl") === "http://test")
    assert(constretto[Double]("double") === 5.5d)

    // Using get returns an Option
    assert(constretto.get[Double]("double") === Some(5.5d))

    val mapProperty = constretto[Map[Int, Float]]("myMap")
    assert(mapProperty.keySet === Set(1,2,3))
    assert(mapProperty.values.toList.sorted === List(2f, 3f, 4f))
    assert(constretto.properties.length === (System.getProperties.size() + System.getenv().size() + 13))

  }

  test("it should throw an ConstrettoExpressionException if a unknown property is provided to Constretto.apply ") {
    val constretto = Constretto(Nil)
    intercept[ConstrettoExpressionException] {
      constretto[String]("unknownProperty")
    }
    
    assert(constretto.get[String]("unknownProperty") === None)
  }

  test("In a Constretto configuration initialized with the json file person.json it should be possible to extract a Person object using the implicit converter") {
    val constretto = Constretto(json("classpath:person.json", "person") :: Nil)
    assert(constretto[Person]("person") === Person("Kaare", 29, None, Address("0767", "Landingsvn")))
  }

}
