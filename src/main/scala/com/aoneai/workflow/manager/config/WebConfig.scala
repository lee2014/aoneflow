package com.aoneai.workflow.manager.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, ResourceHandlerRegistry, WebMvcConfigurer}

/**
  * Web 资源配置类
  */
@Configuration
class WebConfig extends WebMvcConfigurer {

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.addResourceHandler("/webjars/**") //
      .addResourceLocations("classpath:/META-INF/resources/webjars/")
  }
}
