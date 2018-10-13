package me.hdpe.pushfight.server.web

import com.fasterxml.classmate.TypeResolver
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.context.ServletContextAware
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.ZonedDateTime
import javax.servlet.ServletContext

@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration::class)
@Configuration
class WebSwaggerConfig : ServletContextAware {

    class ErrorResponse(val timestamp: ZonedDateTime, val status: Int, val error: String, val details: Array<String>?,
                        val message: String?, val path: String) {
    }

    @Autowired
    private lateinit var typeResolver: TypeResolver

    private lateinit var servletContext: ServletContext

    @Bean
    fun docket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .host("pushfight-app.herokuapp.com")
                .additionalModels(typeResolver.resolve(WebSwaggerConfig.ErrorResponse::class.java))
                .useDefaultResponseMessages(false)
                .ignoredParameterTypes(AuthenticationPrincipal::class.java)
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.hdpe.pushfight.server.web"))
                .paths { !arrayOf("", "/").contains(it) }
                .build()
                .globalResponseMessage(RequestMethod.GET, mutableListOf(internalServerErrorResponse()))
                .globalResponseMessage(RequestMethod.POST, mutableListOf(internalServerErrorResponse()))
                .globalResponseMessage(RequestMethod.PATCH, mutableListOf(internalServerErrorResponse()))
    }

    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo("Pushfight API",
                "We're not scaremongering.", null, null,
                contact(), null, null, emptyList<VendorExtension<*>>())
    }

    private fun contact(): Contact {
        return Contact("Ryan Pickett", "https://hdpe.me", "ryan at hdpe dot me")
    }

    private fun internalServerErrorResponse(): ResponseMessage {
        return ResponseMessageBuilder()
                .code(500)
                .message("Internal server error")
                .responseModel(ModelRef("ErrorResponse"))
                .build()
    }
}