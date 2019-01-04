package com.aoneai.workflow.core.utils

import org.slf4j.{Logger, LoggerFactory}


/**
  * Created By chengli at 03/04/2018
  */
trait Logging {
  val log: Logger = LoggerFactory.getLogger(this.getClass.getName)
}
