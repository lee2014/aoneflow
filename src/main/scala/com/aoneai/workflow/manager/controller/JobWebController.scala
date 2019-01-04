package com.aoneai.workflow.manager.controller

import com.aoneai.workflow.core.entity.{PagerModel, RestResponse}
import com.aoneai.workflow.core.utils.{Logging, TimeUtil}
import com.aoneai.workflow.manager.entity.{Job, JobStatus}
import com.aoneai.workflow.manager.service.JobServiceImpl
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, Pageable, Sort}
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._

/**
  * Created By chengli at 09/04/2018
  */
@Controller
@RequestMapping(path = Array("/job"))
class JobWebController extends Logging {

  private val BUTTONS_TO_SHOW = 3

  @Autowired
  private var jobServiceImpl: JobServiceImpl = _

  /**
    * 转到列表页面
    *
    * @return
    */
  @RequestMapping(value = Array("/listJob"), method = Array(RequestMethod.GET, RequestMethod.POST))
  def listJob(@PageableDefault(size = 10, sort = Array("create_time"), direction = Sort.Direction.DESC) page: Pageable,
              jobName: String,
              status: String,
              model: Model): String = {
    log.info(s"ListJob method params: " +
      s"jobName: $jobName, status: $status, pageSize: ${page.getPageSize}, pageNumber: ${page.getPageNumber}")

    var pageResult: Page[Job] = null

    if (StringUtils.isEmpty(jobName) && StringUtils.isEmpty(status)) {
      pageResult = this.jobServiceImpl.getAllJob(page)
    } else if (StringUtils.isNotEmpty(jobName) && StringUtils.isNotEmpty(status)) {
      pageResult = this.jobServiceImpl.getByJobNameAndStatus("%" + jobName + "%", status, page)
    } else if (StringUtils.isNotEmpty(jobName) && StringUtils.isEmpty(status)) {
      pageResult = this.jobServiceImpl.getByJobName("%" + jobName + "%", page)
    } else if (StringUtils.isNotEmpty(status) && StringUtils.isEmpty(jobName)) {
      pageResult = this.jobServiceImpl.getByStatus(status, page)
    }
    model.addAttribute("jobPage", pageResult)
    val pager = new PagerModel(pageResult.getTotalPages, pageResult.getNumber, BUTTONS_TO_SHOW)
    model.addAttribute("pager", pager)
    model.addAttribute("status", status)
    model.addAttribute("jobName", jobName)

    "/list_job"
  }

  /**
    * 转到添加job页面
    * @return
    */
  @GetMapping(value = Array("/addJobInput"))
  def addInput(model: Model): String = {
    model.addAttribute("job", new Job)
    "/add_job"
  }

  /**
    * 转到添加job页面
    * @return
    */
  @GetMapping(value = Array("/editJobInput"))
  def editInput(jobName: String, model: Model): String = {
    val job = this.jobServiceImpl.getJobByID(jobName).getOrElse(throw new IllegalStateException(jobName + " is not exist"))
    model.addAttribute("job", job)
    "/edit_job"
  }

  /**
    * 添加job
    * @return
    */
  @PostMapping(value = Array("/addJob"))
  @ResponseBody
  def add(@RequestBody job: Job): RestResponse = {
    log.info("Add method params: [job: {}]", job.toString)
    val response = RestResponse()
    try {
      job.createTime = TimeUtil.getCurrentDateTime
      this.jobServiceImpl.addJob(job)
      response.code = RestResponse.SUCCESS_CODE
      response.msg = "添加Job成功"
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("Add a simple job exception: %s,Job: %s", e.getMessage, job.toString)
        log.error(msg)
    }
    response
  }

  @PostMapping(value = Array("/addJobs"))
  @ResponseBody
  def add(@RequestBody jobs: Seq[Job]): RestResponse = {
    val response = RestResponse()
    try {
      this.jobServiceImpl.addJobs(jobs)
      response.code = RestResponse.SUCCESS_CODE
      response.msg = "添加Job成功"
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("Add multiple jobs exception: %s,Job: %s", e.getMessage, jobs)
        log.error(msg)
    }
    response
  }

  /**
    * 修改job
    * @return
    */
  @PutMapping(value = Array("/editJobByJobName"))
  @ResponseBody
  def edit(@RequestBody job: Job): RestResponse = {
    log.info("Edit method params: [job: {}]", job.toString)
    val response = RestResponse()
    try {
      job.updateTime = TimeUtil.getCurrentDateTime
      this.jobServiceImpl.updateJob(job)
      response.code = RestResponse.SUCCESS_CODE
      response.msg = "修改Job成功"
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("Edit method exception: %s,Job: %s", e.getMessage, job.toString)
        log.error(msg)
    }
    response
  }

  /**
    * 重启job
    * @return
    */
  @PutMapping(value = Array("/editWithRestartJobByJobName"))
  @ResponseBody
  def editWithRestartJobByJobName(jobName: String): RestResponse = {
    log.info("EditWithRestartJobByJobName method params: [jobName: {}]", jobName)
    val response = RestResponse()
    try {
      val job = this.jobServiceImpl.getJobByID(jobName).orNull
      if (job != null) {
        job.updateTime = TimeUtil.getCurrentDateTime
        job.status = JobStatus.WAITING
        this.jobServiceImpl.updateJob(job)
        response.code = RestResponse.SUCCESS_CODE
        response.msg = "操作成功"
      } else {
        response.code = RestResponse.FAIL_CODE
        response.msg = "Job不存在"
      }
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("EditWithRestartJobByJobName method exception: %s,jobName: %s", e.getMessage, jobName)
        log.error(msg)
    }
    response
  }

  /**
    * 杀死job
    * @return
    */
  @PutMapping(value = Array("/editWithKillJobByJobName"))
  @ResponseBody
  def editWithKillJobByJobName(jobName: String): RestResponse = {
    log.info("EditWithKillJobByJobName method params: [jobName: {}]", jobName)
    val response = RestResponse()
    try {
      val job = this.jobServiceImpl.getJobByID(jobName).orNull
      if (job != null) {
        job.updateTime = TimeUtil.getCurrentDateTime
        job.status = JobStatus.KILLED
        this.jobServiceImpl.updateJob(job)
        response.code = RestResponse.SUCCESS_CODE
        response.msg = "操作成功"
      } else {
        response.code = RestResponse.FAIL_CODE
        response.msg = "Job不存在"
      }
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("EditWithKillJobByJobName method exception: %s,jobName: %s", e.getMessage, jobName)
        log.error(msg)
    }
    response
  }

  /**
    * 置成功
    * @return
    */
  @PutMapping(value = Array("/editWithForceJobByJobName"))
  @ResponseBody
  def editWithForceJobByJobName(jobName: String): RestResponse = {
    log.info("editWithForceJobByJobName method params: [jobName: {}]", jobName)
    val response = RestResponse()
    try {
      val job = this.jobServiceImpl.getJobByID(jobName).orNull
      if (job != null) {
        job.updateTime = TimeUtil.getCurrentDateTime
        job.status = JobStatus.SUCCESS
        this.jobServiceImpl.updateJob(job)
        response.code = RestResponse.SUCCESS_CODE
        response.msg = "操作成功"
      } else {
        response.code = RestResponse.FAIL_CODE
        response.msg = "Job不存在"
      }
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("editWithForceJobByJobName method exception: %s,jobName: %s", e.getMessage, jobName)
        log.error(msg)
    }
    response
  }

  /**
    * 删除job
    * @return
    */
  @DeleteMapping(value = Array("/delJobByJobName"))
  @ResponseBody
  def delete(jobName: String): RestResponse = {
    log.info("Delete method params: [jobName: {}]", jobName)
    val response = RestResponse()
    try {
      this.jobServiceImpl.deleteByJobName(jobName)
      response.code = RestResponse.SUCCESS_CODE
      response.msg = "操作成功"
    } catch {
      case e: Exception =>
        response.code = RestResponse.FAIL_CODE
        response.msg = e.getMessage
        val msg = String.format("Delete method exception: %s,jobName: %s", e.getMessage, jobName)
        log.error(msg)
    }
    response
  }
}
