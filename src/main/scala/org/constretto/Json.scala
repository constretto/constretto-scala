package org.constretto


sealed trait Json {
  def fold[A](arr: List[Json] => A, obj: Map[String, Json] => A, primitive: String => A) = this match {
    case JArray(data) => arr(data)
    case JObject(data) => obj(data)
    case JPrimitive(value) => primitive(value)
  }
}

case class JArray(data: List[Json]) extends Json

case class JObject(data: Map[String, Json]) extends Json

case class JPrimitive(value: String) extends Json