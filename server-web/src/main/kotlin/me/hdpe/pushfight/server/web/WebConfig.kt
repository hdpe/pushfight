package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
class WebConfig {

    @Bean
    fun errorAttributes(): DefaultErrorAttributes = ReducedErrorAttributes()

    @Bean
    fun objectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
            Jackson2ObjectMapperBuilderCustomizer { builder ->
                builder.modules(WebJacksonModule(), JavaTimeModule(), KotlinModule())
                        .serializationInclusion(JsonInclude.Include.NON_NULL) }
}