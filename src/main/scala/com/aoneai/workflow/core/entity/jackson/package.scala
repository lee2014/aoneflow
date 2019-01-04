package com.aoneai.workflow.core.entity

import java.io.IOException

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Created By chengli at 03/04/2018
  */
package object jackson {
  private[jackson] val OBJECT_MAPPER: ObjectMapper = new ObjectMapper
  OBJECT_MAPPER.registerModule(DefaultScalaModule)
  OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)

  def fromString[T](string: String, clazz: Class[T]): T = {
    try {
      OBJECT_MAPPER.readValue(string, clazz)
    } catch {
      case e: IOException =>
        throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object")
    }
  }

  def toString(value: Any): String = {
    try {
      OBJECT_MAPPER.writeValueAsString(value)
    } catch {
      case e: JsonProcessingException =>
        throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String")
    }
  }

  def toJsonNode(value: String): JsonNode = {
    try {
      OBJECT_MAPPER.readTree(value)
    } catch {
      case e: IOException => throw new IllegalArgumentException(e)
    }
  }

  def clone[T](value: T): T = fromString(toString(value), value.getClass.asInstanceOf[Class[T]])
}
