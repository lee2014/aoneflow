package com.aoneai.workflow.manager.service

import com.aoneai.workflow.core.utils.{Logging, TimeUtil}
import com.aoneai.workflow.manager.config.JobExecutorConfig
import com.aoneai.workflow.manager.entity.{Job, JobStatus}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
  * Created By chengli at 07/04/2018
  */
@Service
class JobExecutorPool extends Logging {

  @Autowired
  private var exeConf: JobExecutorConfig = _

  @Autowired
  private var jobServiceImpl: JobServiceImpl = _

  @Autowired
  private var jobRequest: JobRequest = _

  def prepareRunJob(job: Job): Unit = {
    try {
      log.info(s"Job(${job.jobName}) is ready to run.")
      this.jobServiceImpl.runJob(job) // set a job to run in db
    } catch {
      case re: RuntimeException => log.error(re.getMessage)
    }
  }

  private def finishRunJob(job: Job, code: Int): Unit = {
    if (code != 0) { job.failedTime += 1 }
    job.status = if (code == 0) JobStatus.SUCCESS else JobStatus.FAILED
    job.endTime = TimeUtil.getCurrentDateTime

    this.jobServiceImpl.updateJob(job)
    log.info(s"Job(${job.jobName}) finishes to run.")
  }

  @Async("jobExecutorAsyncPool")
  def runJob(job: Job): Unit = {
    log.info(s"Start job(${job.jobName})")
    log.debug(s"Thread name: ${Thread.currentThread.getName}")
    var code: Int = jobRequest.run(job)

    // Disable retry when job failed.
    if (exeConf.enableRetry) {
      var retryCnt = exeConf.retryCount
      while (retryCnt >= 0 && code != 0) {
        retryCnt -= 1
        log.info(s"Retry job(${job.jobName})")
        Thread.sleep(exeConf.retrySleepTime)
        code = jobRequest.run(job)
      }
    }

    // 设置任务结束运行状态
    finishRunJob(job, code)
  }
}
