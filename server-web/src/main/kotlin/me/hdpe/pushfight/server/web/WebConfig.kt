package me.hdpe.pushfight.server.web

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {

    @Bean
    fun errorAttributes(): DefaultErrorAttributes = ReducedErrorAttributes()
}