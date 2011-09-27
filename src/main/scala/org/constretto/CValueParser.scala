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
package org.constretto

import model.{CPrimitive, CObject, CArray, CValue}

/**
 * @author jteigen
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
object CValueParser {

  import collection.JavaConverters._

  def handleArray(cArray: CArray): JArray = JArray(cArray.data().asScala.map(handle).toList)

  def handleObject(cObject: CObject): JObject = {
    JObject(cObject.data().asScala.map {
      e =>
        val key = e._1
        val value = e._2
        key -> handle(value)
    }.toMap)
  }

  def handle(cValue: CValue): Json = {
    if (cValue.isPrimitive) JPrimitive(cValue.asInstanceOf[CPrimitive].value())
    else if (cValue.isArray) handleArray(cValue.asInstanceOf[CArray])
    else handleObject(cValue.asInstanceOf[CObject])
  }


  def parse(v: CValue): Json = handle(v)
}