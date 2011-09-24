package org.constretto

import java.lang.reflect.Type
import com.google.gson._

object GsonParser {
  val builder = new GsonBuilder()
  builder.registerTypeAdapter(classOf[Json], new JsonDeserializer[Json] {

    import collection.JavaConverters._

    def handleArray(jsonArray: JsonArray): JArray = JArray(jsonArray.iterator().asScala.map(handle).toList)

    def handleObject(jsonObject: JsonObject): JObject = {
      JObject(jsonObject.entrySet().asScala.map {
        e =>
          val key = e.getKey
          val value = e.getValue
          key -> handle(value)
      }.toMap)
    }

    def handle(json: JsonElement): Json = {
      if (json.isJsonNull) null
      else if (json.isJsonPrimitive) JPrimitive(json.getAsString)
      else if (json.isJsonArray) handleArray(json.getAsJsonArray)
      else handleObject(json.getAsJsonObject)
    }

    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) = handle(json)
  })

  def parse(s: String): Json = builder.create().fromJson(s, classOf[Json])
}