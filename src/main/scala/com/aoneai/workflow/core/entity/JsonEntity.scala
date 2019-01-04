package com.aoneai.workflow.core.entity

import javax.persistence.MappedSuperclass

import com.aoneai.workflow.core.entity.jackson.{JsonBinaryType, JsonStringType}
import org.hibernate.annotations.{TypeDef, TypeDefs}

/**
  * Created By chengli at 03/04/2018
  */
@TypeDefs(Array(
  new TypeDef(name = "json", typeClass = classOf[JsonStringType]),
  new TypeDef(name = "jsonb", typeClass = classOf[JsonBinaryType])
))
@MappedSuperclass
class JsonEntity
