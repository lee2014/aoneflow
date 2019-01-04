package com.aoneai.workflow.manager.service

import com.aoneai.workflow.core.utils.{Logging, TimeUtil}
import com.aoneai.workflow.manager.entity.{Job, JobStatus}
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created By chengli at 07/04/2018
  */
@Service
class JobScheduler extends Logging {

  @Autowired
  private var jobServiceImpl: JobServiceImpl = _

  @Autowired
  private var jobExecutor: JobExecutorPool = _

  private var _shutdown: Boolean = false

  private var jobSnapshot = mutable.HashMap[String, Job]()

  //Array of job inspect function
  private val inspects = Seq[Function[Job, (Boolean, String)]](checkScheduleTime, checkJobDependencies)

  @PostConstruct
  def init(): Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = _shutdown = true
    })
  }

  private def mergeSnapShot(jobs: Seq[Job]): Unit = {
    jobSnapshot ++= jobs.map(job => job.jobName -> job).toMap
  }

  /**
    * 检查job是否已到调度时间
    *
    * @param job
    * @return
    */
  private def checkScheduleTime(job: Job): (Boolean, String) = {
    if (job.scheduleTime > TimeUtil.getCurrentDateTime) {
      (false, s"Job(${job.jobName}) not at schedule time(${job.scheduleTime})")
    } else {
      (true, "")
    }
  }

  /**
    * 检查依赖任务的状态
    *
    * @param job
    * @return
    */
  private def checkJobDependencies(job: Job): (Boolean, String) = {
    if (job.deps != null && job.deps.nonEmpty) {
      job.deps.foreach { parentJob =>
        val pStatus = jobServiceImpl.findStatusByJobName(parentJob)
        if (pStatus != JobStatus.SUCCESS) {
          return (false, s"Parent($parentJob) of job(${job.jobName}) is not ready!")
        }
      }
    }
    (true, "")
  }

  // check the condition of a job to run
  private def jobInspection(job: Job): (Boolean, String) = {
    inspects.foreach { func =>
      val (code, msg) = func(job)
      if (!code) return (code, msg)
    }

    (true, "")
  }

  @Scheduled(fixedDelay = 10000)
  def runSchedule(): Unit = {
    log.info(s"Run jobScheduler at ${TimeUtil.getCurrentDateTime}")

    if (!_shutdown) {
      // 获取等待执行的job
      val jobs = jobServiceImpl.findWaitingJob
      mergeSnapShot(jobs)

      // 保存正在执行的job
      val toRunJobs = ArrayBuffer.empty[Job]

      jobSnapshot.values.foreach { job =>
        try {
          // 运行无需任何条件，强制执行的job
          if (job.force) {
            scheduleJob(job)
            toRunJobs += job
          } else {
            // 检查任务是否到达指定的调度执行时间、检查任务依赖是否都执行成功。如果两项条件都符合，则执行任务
            val (code: Boolean, msg: String) = jobInspection(job)

            if (code) {
              scheduleJob(job)
              toRunJobs += job
            } else {
              log.debug(msg)
            }
          }
        } catch {
          case e: Exception =>
            log.error(s"Fail to schedule job ${job.jobName}")
            log.error(s"The error message is ${e.getMessage}")
        }
      }

      // 从快照中移除正在执行的任务
      jobSnapshot --= toRunJobs.map(_.jobName)
    }
  }

  def scheduleJob(job: Job): Unit = {
    jobExecutor.prepareRunJob(job)
    jobExecutor.runJob(job)
  }
}
