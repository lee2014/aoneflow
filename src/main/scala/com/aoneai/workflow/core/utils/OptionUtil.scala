package com.aoneai.workflow.core.utils

import java.util.{Optional => JOption}

import scala.language.implicitConversions

/**
  * Created By chengli at 03/04/2018
  */
object OptionUtil {
  implicit final class RichJOption[T](val optional: JOption[T]) {
    def asScala: Option[T] = optional match {
      case null => null
      case _ => if (optional.isPresent) Option(optional.get()) else None
    }
  }

  implicit final class RichOption[T](val option: Option[T]) {
    def asJava: JOption[T] = option match {
      case null => null
      case _ => if (option.isDefined) JOption.of(option.get) else JOption.empty()
    }
  }

  implicit def toJavaOptional[T](sOption: Option[T]): JOption[T] = RichOption(sOption).asJava

  implicit def toScalaOption[T](jOption: JOption[T]): Option[T] = RichJOption(jOption).asScala
}
