package com.aoneai.workflow.manager.service

import java.util
import javax.annotation.PostConstruct

import com.aoneai.workflow.core.utils.OptionUtil._
import com.aoneai.workflow.core.utils.{Logging, TimeUtil}
import com.aoneai.workflow.manager.dao.JobRepository
import com.aoneai.workflow.manager.entity.{Job, JobStatus}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

/**
  * 提供job信息业务逻辑操作
  */
@Service
@Transactional
class JobServiceImpl extends Logging {

  @Autowired
  private var jobRepository: JobRepository = _

  @PostConstruct
  def init(): Unit = {
    log.info("Updating all existing running/waiting job to killed.")
    jobRepository.updateNotDoneJobWithKilled()
    log.info("Updated all existing running/waiting job to killed.")
  }

  /**
    * 根据主键查询job信息
    * @return
    */
  def getJobByID(jobName: String): Option[Job] =
    this.jobRepository.findById(jobName)

  /**
    * 获取所有job
    * @return
    */
  def getAllJob(pageable: Pageable): Page[Job] =
    this.jobRepository.getAllJobByPage(pageable)

  def getByStatus(status: String, pageable: Pageable): Page[Job] =
    this.jobRepository.getByStatusAndPage(status, pageable)

  def getByJobName(jobName: String, pageable: Pageable): Page[Job] =
    this.jobRepository.getByJobNameAndPage(jobName, pageable)

  def getByJobNameAndStatus(jobName: String, status: String, pageable: Pageable): Page[Job] =
    this.jobRepository.getByJobNameAndStatusAndPage(jobName, status, pageable)

  /**
    * 添加job
    * @return
    */
  def addJob(job: Job): Job = {
    if (this.jobRepository.existsByJobName(job.jobName) > 0)
      throw new IllegalArgumentException(s"Job ${job.jobName} has existed!")

    job.createTime = TimeUtil.getCurrentDateTime
    job.status = JobStatus.WAITING
    job.force = false
    job.failedTime = 0

    this.jobRepository.save(job)
  }

  def addJobs(jobs: Seq[Job]): util.List[Job] = {
    val appendJobs = jobs.filter(job => !jobRepository.existsById(job.jobName)).map { job =>

      job.createTime = TimeUtil.getCurrentDateTime
      job.status = JobStatus.WAITING
      job.force = false
      job.failedTime = 0
      job
    }
    this.jobRepository.saveAll[Job](appendJobs)
  }

  /**
    * 删除job
    */
  def deleteByJobName(jobName: String): Unit = {
    this._checkNotRunningJob(jobName)
    this.jobRepository.deleteById(jobName)
  }

  /**
    * 更新job
    * @return
    */
  def updateJob(job: Job): Job =
    this.jobRepository.save(job)

  /**
    * 检查job是否运行
    * @return
    */
  def _checkNotRunningJob(jobName: String): String = {
    val status = this.findStatusByJobName(jobName)
    status match {
      case JobStatus.NONE => throw new IllegalArgumentException(s"Job $jobName does not exist!")
      case JobStatus.RUNNING => throw new RuntimeException(s"Job $jobName is running!")
      case _ => status
    }
  }

  /**
    * 运行job
    */
  def runJob(job: Job): Unit = {
    _checkNotRunningJob(job.jobName) // 检查job是否已经在运行，如果已经运行，就不重复运行job

    // 设置job的运行状态、开始运行时间
    job.status = JobStatus.RUNNING
    job.startTime = TimeUtil.getCurrentDateTime

    // 更新job状态
    this.updateJob(job)
  }

  /**
    * 查询等待执行的job
    * @return
    */
  def findWaitingJob: util.List[Job] =
    this.jobRepository.getByStatus(JobStatus.WAITING)

  /**
    * 根据jobName查询job
    * @return
    */
  def findStatusByJobName(jobName: String): String =
    this.jobRepository.getStatusByJobName(jobName).getOrElse(JobStatus.NONE)


  /**
    * 增加失败次数
    * @return
    */
  def updateAndIncJobFailedTime(jobName: String): Int =
    this.jobRepository.updateAndIncJobFailedTime(jobName)

}
