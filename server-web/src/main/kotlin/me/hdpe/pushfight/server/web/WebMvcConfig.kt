package me.hdpe.pushfight.server.web

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Configuration
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(val messageSource: MessageSource) : WebMvcConfigurer {

    override fun getValidator(): Validator {
        val factory = LocalValidatorFactoryBean()
        factory.setValidationMessageSource(messageSource)
        return factory
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/docs").setViewName("redirect:/docs/")
        registry.addViewController("/docs/").setViewName("forward:/docs/index.html")
    }
}