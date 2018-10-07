package me.hdpe.pushfight.server.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.context.ServletContextAware
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.Contact
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import javax.servlet.ServletContext

@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration::class)
class WebSwaggerConfig : ServletContextAware {

//    @ApiResponse
//    class ErrorResponse {
//
//        var error: String
//    }

//    @Autowired
//    private lateinit var typeResolver: TypeResolver

    private lateinit var servletContext: ServletContext

    @Bean
    fun docket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .host("localhost")
//                .pathProvider(object : RelativePathProvider(servletContext) {
//                    override fun getApplicationBasePath(): String {
//                        return "/"
//                    }
//                })
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.hdpe.pushfight.server.web"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(AuthenticationPrincipal::class.java)
                .securitySchemes(mutableListOf(ApiKey("token", "Bearer", "header")))
//                .useDefaultResponseMessages(false)
//                .globalResponseMessage(GET, newArrayList<ResponseMessage>(errorResponse()))
//                .globalResponseMessage(POST, newArrayList<ResponseMessage>(errorResponse()))
//                .additionalModels(typeResolver!!.resolve(ApiSwaggerConfig.ErrorResponse::class.java))
//                .directModelSubstitute(Char::class.java, String::class.java)
//                .directModelSubstitute(LocalTime::class.java, String::class.java)
//                .tags(
//                )
    }

    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo("Pushfight API",
                "Yup", null, null,
                contact(), null, null, emptyList<VendorExtension<*>>())
    }

    private fun contact(): Contact {
        return Contact("Ryan Pickett", "https://hdpe.me", "ryan at hdpe dot me")
    }

//    private fun errorResponse(): ResponseMessage {
//        return ResponseMessageBuilder()
//                .code(500)
//                .message("Wrapped response payload")
//                .responseModel(ModelRef("ErrorResponse"))
//                .build()
//    }
}