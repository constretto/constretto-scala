package org.constretto

import org.scalatest._
import org.constretto.Constretto._
import org.constretto.exception.ConstrettoExpressionException
import demo.{Address, Person}

/**
 *
 * @author zapodot at gmail dot com
 */
class ConstrettoTest extends FlatSpec with Matchers {

  "An empty Constretto configuration " should "have only system properties and environment variables" in {
    val constretto = Constretto(Nil)

    constretto.properties.length should be (System.getProperties.size() + System.getenv().size())
  }

  "A Constretto configuration initialized with a property file" should "contain all system properties, environment variables and all properties set in the file" in {

    val constretto = Constretto(properties("classpath:test.properties") :: Nil, "test")

    constretto[String]("baseUrl") should be ("http://test")
    constretto[Double]("double") should be (5.5d)

    // Using get returns an Option
    constretto.get[Double]("double") should be (Some(5.5d))

    val mapProperty = constretto[Map[Int, Float]]("myMap")
    mapProperty.keySet should be (Set(1,2,3))
    mapProperty.values should contain theSameElementsAs Seq(2f, 3f, 4f)
    constretto.properties.length should be (System.getProperties.size() + System.getenv().size() + 13)

  }

  it should "throw an ConstrettoExpressionException if a unknown property is provided to Constretto.apply " in {
    val constretto = Constretto(Nil)
    a [ConstrettoExpressionException] should be thrownBy {
      constretto[String]("unknownProperty")
    }
    constretto.get[String]("unknownProperty") should be (None)
  }

  "In a Constretto configuration initialized with the json file 'person.json it " should "be possible to extract a Person object using the implicit converter" in {
    val constretto = Constretto(json("classpath:person.json", "person") :: Nil)
    constretto[Person]("person") should be (Person("Kaare", 29, None, Address("0767", "Landingsvn")))
  }

}
