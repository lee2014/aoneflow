package com.aoneai.workflow.manager

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.{DeserializationFeature, MapperFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Primary}
import org.springframework.scheduling.annotation.{EnableAsync, EnableScheduling}
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
  * Created By chengli at 03/04/2018
  */
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
@SpringBootApplication
class Application {
  @Primary @Bean
  def mapper: ObjectMapper = {
    val mapper = new ObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    mapper
  }
}

object Application extends App {
  SpringApplication.run(classOf[Application], args:_*)
}