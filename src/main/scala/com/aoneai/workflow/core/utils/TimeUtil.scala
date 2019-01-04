package com.aoneai.workflow.core.utils

import java.text.{ParseException, SimpleDateFormat}

import com.aoneai.workflow.core.common.Constants._
import java.util.{Calendar, Date, TimeZone}

/**
  * Created By chengli at 07/04/2018
  */
object TimeUtil {

  TimeZone.setDefault(DEFAULT_TIMEZONE)

  final val DATE_FORMAT: String = "yyyy-MM-dd"
  final val DATE_FORMAT_OBJECT = new SimpleDateFormat(DATE_FORMAT)
  final val DATE_TIME_FORMAT: String = "yyyy-MM-dd HH:mm:ss"
  final val DATE_TIME_FORMAT_OBJECT = new SimpleDateFormat(DATE_TIME_FORMAT)

  final val ONE_DAY_IN_MILLIS: Long = 24 * 3600 * 1000L

  def getCurrentDate: String = {
    val cal: Calendar = Calendar.getInstance(DEFAULT_TIMEZONE)
    DATE_FORMAT_OBJECT.format(cal.getTime)
  }

  def getCurrentDateTime: String = {
    val cal: Calendar = Calendar.getInstance(DEFAULT_TIMEZONE)
    DATE_TIME_FORMAT_OBJECT.format(cal.getTime)
  }

  @throws(classOf[ParseException])
  def parseDate(dateStr: String): Date = {
    DATE_FORMAT_OBJECT.parse(dateStr)
  }

  @throws(classOf[ParseException])
  def parseDateTime(dateStr: String): Date = {
    DATE_TIME_FORMAT_OBJECT.parse(dateStr)
  }
}
