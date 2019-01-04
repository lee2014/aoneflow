package com.aoneai.workflow.core.entity

import scala.beans.BeanProperty

class PagerModel {

  var buttonsToShow = 5

  @BeanProperty
  var startPage = 0

  @BeanProperty
  var endPage = 0

  def this(totalPages: Int, currentPage: Int, buttonsToShow: Int) {
    this()
    setButtonsToShow(buttonsToShow)
    val halfPagesToShow = getButtonsToShow / 2
    if (totalPages <= getButtonsToShow) {
      setStartPage(1)
      setEndPage(totalPages)
    }
    else if (currentPage - halfPagesToShow <= 0) {
      setStartPage(1)
      setEndPage(getButtonsToShow)
    }
    else if (currentPage + halfPagesToShow == totalPages) {
      setStartPage(currentPage - halfPagesToShow)
      setEndPage(totalPages)
    }
    else if (currentPage + halfPagesToShow > totalPages) {
      setStartPage(totalPages - getButtonsToShow + 1)
      setEndPage(totalPages)
    }
    else {
      setStartPage(currentPage - halfPagesToShow)
      setEndPage(currentPage + halfPagesToShow)
    }
  }

  def getButtonsToShow: Int = buttonsToShow

  def setButtonsToShow(buttonsToShow: Int): Unit = {
    if (buttonsToShow % 2 != 0) this.buttonsToShow = buttonsToShow
    else throw new IllegalArgumentException("Must be an odd value!")
  }

  override def toString = s"PagerModel(buttonsToShow=$buttonsToShow, startPage=$startPage, endPage=$endPage)"
}
