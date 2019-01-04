package com.aoneai.workflow.core.entity.jackson

import java.sql.{CallableStatement, PreparedStatement, SQLException}

import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.`type`.descriptor.{ValueBinder, WrapperOptions}
import org.hibernate.`type`.descriptor.java.JavaTypeDescriptor
import org.hibernate.`type`.descriptor.sql.BasicBinder

/**
  * Created By chengli at 03/04/2018
  */
class JsonBinarySqlTypeDescriptor extends AbstractJsonSqlTypeDescriptor {
  override def getBinder[X](javaTypeDescriptor: JavaTypeDescriptor[X]): ValueBinder[X] =
    new BasicBinder[X](javaTypeDescriptor, this) {

      @throws[SQLException]
      override def doBind(st: PreparedStatement,
                          value: X,
                          index: Int,
                          options: WrapperOptions): Unit = {
        st.setObject(index, javaTypeDescriptor.unwrap(value, classOf[JsonNode], options), getSqlType)
      }

      @throws[SQLException]
      override def doBind(st: CallableStatement,
                          value: X,
                          name: String,
                          options: WrapperOptions): Unit = {
        st.setObject(name, javaTypeDescriptor.unwrap(value, classOf[JsonNode], options), getSqlType)
      }
    }
}

object JsonBinarySqlTypeDescriptor {
  val INSTANCE = new JsonBinarySqlTypeDescriptor
}
