package me.hdpe.pushfight.server.web

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(val messageSource: MessageSource) : WebMvcConfigurer {

    @Bean("validator")
    override fun getValidator(): Validator? {
        val factory = LocalValidatorFactoryBean()
        factory.setValidationMessageSource(messageSource)
        return factory
    }
}