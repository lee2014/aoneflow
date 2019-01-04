package com.aoneai.workflow.core.entity.jackson

import java.util.Properties

import com.aoneai.workflow.core.entity.jackson
import org.hibernate.`type`.descriptor.WrapperOptions
import org.hibernate.`type`.descriptor.java.{AbstractTypeDescriptor, MutableMutabilityPlan}
import org.hibernate.usertype.DynamicParameterizedType

/**
  * Created By chengli at 03/04/2018
  */
class JsonTypeDescriptor extends AbstractTypeDescriptor[Any](classOf[Any], new MutableMutabilityPlan[Any]() {
  override protected def deepCopyNotNull(value: Any): Any = jackson.clone(value)
}) with DynamicParameterizedType {
  private var jsonObjectClass: Class[_] = _

  override def setParameterValues(parameters: Properties): Unit = {
    jsonObjectClass = parameters.get(DynamicParameterizedType.PARAMETER_TYPE)
      .asInstanceOf[DynamicParameterizedType.ParameterType]
      .getReturnedClass
  }

  override def areEqual(one: Any, another: Any): Boolean = {
    if (one equals another) return true
    if (one == null || another == null) return false
    jackson.toJsonNode(toString(one)) equals jackson.toJsonNode(toString(another))
  }

  override def toString(value: Any): String = jackson.toString(value)

  override def fromString(string: String): Any = jackson.fromString(string, jsonObjectClass)

  @SuppressWarnings(Array("unchecked"))
  override def unwrap[X](value: Any, `type`: Class[X], options: WrapperOptions): X = {
    if (value == null) return null.asInstanceOf[X]
    if (classOf[String].isAssignableFrom(`type`)) return toString(value).asInstanceOf[X]
    if (classOf[Any].isAssignableFrom(`type`)) return jackson.toJsonNode(toString(value)).asInstanceOf[X]
    throw unknownUnwrap(`type`)
  }

  override def wrap[X](value: X, options: WrapperOptions): Any = {
    if (value == null) return null
    fromString(value.toString)
  }
}

