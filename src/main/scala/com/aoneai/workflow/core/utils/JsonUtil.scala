package com.aoneai.workflow.core.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, MapperFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.JavaConverters._

/**
  * Created By chengli at 03/04/2018
  */
object JsonUtil {
  private[core] val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
  mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

  def toJson(value: java.util.Map[String, AnyRef]): String = {
    toJson(value.asScala)
  }

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  def toBytes(value: Any): Array[Byte] = {
    mapper.writeValueAsBytes(value)
  }

  def toScalaMap(value: Any): Map[String, Object] = {
    mapper.convertValue(value, classOf[Map[String, Object]])
  }

  def toScalaMap(value: String): Map[String, Object] = {
    mapper.convertValue(value, classOf[Map[String, Object]])
  }

  def toJavaMap(value: Any): java.util.Map[String, Object] = {
    mapper.convertValue(value, classOf[java.util.Map[String, Object]])
  }

  def toJavaMap(value: String): java.util.HashMap[String, Object] = {
    mapper.readValue(value, new TypeReference[java.util.Map[String, Object]](){})
  }

  def fromMap[T](value: java.util.Map[String, Any], clazz: Class[T]): T = {
   fromMap(value.asScala.toMap, clazz)
  }

  def fromMap[T](value: Map[String, Any], clazz: Class[T]): T = {
    mapper.convertValue(value, clazz)
  }

  def fromJson[T](value: String, clazz: Class[T]): T = {
    mapper.readValue(value, clazz)
  }
}
