package com.aoneai.workflow.manager.entity

import java.util
import javax.persistence._

import com.aoneai.workflow.core.entity.JsonEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Type

import scala.beans.BeanProperty

/**
  * Created By chengli at 03/04/2018
  */
@Entity
@Table(name = "jobs")
class Job extends JsonEntity {

  @Id
  @Column(name = "job_name", nullable = false, unique = true)
  @BeanProperty
  var jobName: String = _ //任务名称

  @Column(name = "command", nullable = false,columnDefinition = "text")
  @BeanProperty
  var command: String = _ //执行调度的命令

  @Type(`type` = "json")
  @Column(name = "deps", nullable = true, columnDefinition = "json")
  @BeanProperty
  var deps: java.util.LinkedList[String] = _ //任务依赖：以英文逗号分隔

  @Column(name = "status", nullable = true)
  @BeanProperty
  var status: String = _  //job状态：WAITING-未开始、RUNNING-运行中、SUCCESS-运行成功、FAILED-运行失败

  @Column(name = "business_time", nullable = false)
  @BeanProperty
  var businessTime: String = _ //业务时间：每个程序运行所处理的数据日期

  @Column(name = "schedule_time", nullable = false)
  @BeanProperty
  var scheduleTime: String = _ //业务时间：每个程序运行所处理的数据日期

  @Column(name = "start_time", nullable = true)
  @BeanProperty
  var startTime: String = _  //执行开始时间

  @Column(name = "end_time", nullable = true)
  @BeanProperty
  var endTime: String = _ //执行结束时间

  @Column(name = "description", nullable = true)
  @BeanProperty
  var description: String = _ //描述

  @Column(name = "failed_time", nullable = true)
  @BeanProperty
  var failedTime: Int = 0 //执行失败次数，执行失败后每次加1

  @Column(name = "force_flag", nullable = true)
  @BeanProperty
  var force: Boolean = false

  @Column(name = "create_time", nullable = true)
  @BeanProperty
  var createTime:String = _

  @Column(name = "update_time", nullable = true)
  @BeanProperty
  var updateTime:String = _

  @Transient
  @JsonIgnore
  def addDep(dep: String): this.type = {
    if (deps == null) deps = new util.LinkedList[String]()
    deps.add(dep)
    this
  }

  override def toString = s"Job($jobName, $command, $deps, $status, $businessTime, $scheduleTime)"
}

/**
  * Job状态
  */
object JobStatus {
  val NONE = "NONE"
  val WAITING = "WAITING"
  val RUNNING = "RUNNING"
  val SUCCESS = "SUCCESS"
  val FAILED = "FAILED"
  val KILLED = "KILLED"
}