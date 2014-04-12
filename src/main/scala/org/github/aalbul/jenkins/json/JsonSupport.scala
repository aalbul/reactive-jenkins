package org.github.aalbul.jenkins.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.{RequiredPropertiesSchemaModule, ScalaObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.joda.JodaModule

/**
 * Created by nuru on 3/31/14.
 *
 * Mix-in to get support of json serialization / deserialization
 */
trait JsonSupport {
  protected val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(new DefaultScalaModule with RequiredPropertiesSchemaModule)
  mapper.registerModule(new JodaModule())
  mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
