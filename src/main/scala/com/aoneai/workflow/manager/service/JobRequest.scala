package com.aoneai.workflow.manager.service

import java.io.File

import com.aoneai.workflow.core.utils.Logging
import com.aoneai.workflow.manager.entity.Job
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
  * Created By chengli at 08/04/2018
  */
@Component
class JobRequest extends Logging {

  @Value(value = "${logging.flow.path}")
  var logPath: String = _

  @PostConstruct
  def init(): Unit ={
    val workSpace: File = new File(logPath)
    workSpace.mkdirs
  }

  def run(job:Job): Int = {
    var code = 0
    try {
      val errLog: File = new File(s"$logPath/${job.jobName}.error")
      val outLog: File = new File(s"$logPath/${job.jobName}.out")

      !errLog.exists && errLog.createNewFile
      !outLog.exists && outLog.createNewFile

      // /bin/bash表示bash脚本程序，-c用来启动bash程序
      val procBuilder: ProcessBuilder = new ProcessBuilder("/bin/bash", "-c", job.command)
      procBuilder.redirectOutput(outLog)
      procBuilder.redirectError(errLog)

      val env = procBuilder.environment
      env.put("system_bizdate", job.businessTime)

      val proc: Process = procBuilder.start()
      code = proc.waitFor()

      log.debug(s"Job ${job.jobName} execute command is:${procBuilder.command()}")
    } catch {
      case _: Exception => code = -1
    }

    log.info(s"Job(${job.jobName}) completed with code($code)")
    code
  }

  //  def isAlive: Boolean = {
  //    proc != null && proc.isAlive
  //  }
  //
  //  def getCode: Int = {
  //    Option(proc).map(_.exitValue).getOrElse(-1)
  //  }
  //
  //  def destroy(): Unit = {
  //    if (proc != null) {
  //      proc.destroyForcibly()
  //    } else {
  //      throw new RuntimeException(s"Job $name does not exist yet, you cannot destroy it!")
  //    }
  //  }
}
