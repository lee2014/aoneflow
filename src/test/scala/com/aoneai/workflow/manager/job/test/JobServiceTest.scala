package com.aoneai.workflow.manager.job.test

import java.util
import java.util.Date

import com.aoneai.workflow.manager.entity.Job
import com.aoneai.workflow.manager.service.JobServiceImpl
import org.apache.commons.lang.time.DateFormatUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.{DataJpaTest, TestEntityManager}
import org.springframework.test.context.junit4.SpringRunner

@RunWith(classOf[SpringRunner])
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
class JobServiceTest {

  @Autowired
  var jobServiceImpl:JobServiceImpl = _

  @Autowired
  val entityManager:TestEntityManager = null

  @Test
  def testAdd(): Unit ={
    try {
      val job = new Job
      job.setJobName("test")
      job.setCommand("ll")
      job.addDep("dddd")
      job.setBusinessTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
      job.setStartTime("")
      job.setEndTime("")
      job.setDescription("")

      this.jobServiceImpl.addJob(job)
    } catch {
      case  e:Exception=>e.printStackTrace()
    }
  }
}
