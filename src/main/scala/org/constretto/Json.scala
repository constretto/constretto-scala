package org.constretto


sealed trait Json {
  def fold[A](arr: List[Json] => A, obj: JObject => A, primitive: String => A) = this match {
    case JArray(data) => arr(data)
    case ject@JObject(data) => obj(ject)
    case JPrimitive(value) => primitive(value)
  }
}

case class JArray(data: List[Json]) extends Json

case class JObject(data: Map[String, Json]) extends Json {
  def apply[A](name:String)(implicit converter:Converter[A]) = converter.convert(data(name))
  def get[A](name:String)(implicit converter:Converter[A]) = data.get(name).map(converter.convert)
}

case class JPrimitive(value: String) extends Json