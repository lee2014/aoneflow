package com.aoneai.workflow.core.entity.jackson

import java.sql.{CallableStatement, ResultSet, SQLException, Types}

import org.hibernate.`type`.descriptor.{ValueExtractor, WrapperOptions}
import org.hibernate.`type`.descriptor.java.JavaTypeDescriptor
import org.hibernate.`type`.descriptor.sql.{BasicExtractor, SqlTypeDescriptor}

/**
  * Created By chengli at 03/04/2018
  */
abstract class AbstractJsonSqlTypeDescriptor extends SqlTypeDescriptor {
  override def getSqlType: Int = Types.OTHER

  override def canBeRemapped: Boolean = true

  override def getExtractor[X](javaTypeDescriptor: JavaTypeDescriptor[X]): ValueExtractor[X] =
    new BasicExtractor[X](javaTypeDescriptor, this) {
      @throws[SQLException]
      override def doExtract(rs: ResultSet,
                             name: String,
                             options: WrapperOptions): X =
        javaTypeDescriptor.wrap(rs.getObject(name), options)

      @throws[SQLException]
      override def doExtract(statement: CallableStatement,
                             index: Int,
                             options: WrapperOptions): X =
        javaTypeDescriptor.wrap(statement.getObject(index), options)

      @throws[SQLException]
      override def doExtract(statement: CallableStatement,
                             name: String,
                             options: WrapperOptions): X =
        javaTypeDescriptor.wrap(statement.getObject(name), options)
    }
}
