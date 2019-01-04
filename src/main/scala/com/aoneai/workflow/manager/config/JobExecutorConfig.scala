package com.aoneai.workflow.manager.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
  * Created By chengli at 09/04/2018
  */
@Configuration
@ConfigurationProperties(prefix = "job.executor.category")
class JobExecutorConfig {

  var retry: Boolean = true

  var retryCount: Int = 3

  var retrySleepTime: Int = 5000

  def enableRetry: Boolean = retry

  def disableRetry: Boolean = !enableRetry
}
