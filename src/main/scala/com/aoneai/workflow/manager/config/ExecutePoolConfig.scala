package com.aoneai.workflow.manager.config

import java.util.concurrent.{Executor, Executors}

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.scheduling.annotation.EnableAsync

/**
  * Spring 线程池配置类
  */
@Configuration
@EnableAsync(proxyTargetClass = true)
@ConfigurationProperties(prefix = "job.executor.thread")
class ExecutePoolConfig {
  var num: Integer = 5

  @Bean
  def jobExecutorAsyncPool: Executor = {
    Executors.newWorkStealingPool(num)
  }
}
