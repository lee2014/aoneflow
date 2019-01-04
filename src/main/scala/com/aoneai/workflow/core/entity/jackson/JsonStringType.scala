package com.aoneai.workflow.core.entity.jackson

import java.util.Properties

import org.hibernate.`type`.AbstractSingleColumnStandardBasicType
import org.hibernate.usertype.DynamicParameterizedType

/**
  * Created By chengli at 03/04/2018
  */
class JsonStringType extends AbstractSingleColumnStandardBasicType[Any](
  JsonStringSqlTypeDescriptor.INSTANCE,
  new JsonTypeDescriptor
) with DynamicParameterizedType {
  override def getName: String = "json"

  override protected def registerUnderJavaType: Boolean = true

  override def setParameterValues(parameters: Properties): Unit = {
    getJavaTypeDescriptor
      .asInstanceOf[JsonTypeDescriptor]
      .setParameterValues(parameters)
  }
}

object JsonStringType {
  def apply: JsonStringType = new JsonStringType()
}
