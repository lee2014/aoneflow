package com.aoneai.workflow.core.entity

import scala.beans.BeanProperty

class RestResponse{
  @BeanProperty
  var code: Int = 0
  @BeanProperty
  var msg: String = ""
  @BeanProperty
  var data: Object = _
}

object RestResponse {

  val SUCCESS_CODE: Int = 0
  val FAIL_CODE: Int = 100

  def apply(): RestResponse = new RestResponse()

  def apply(code: Int, msg: String): RestResponse = {
    val response = RestResponse()
    response.setCode(code)
    response.setMsg(msg)
    response
  }
}