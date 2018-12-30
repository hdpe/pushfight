package me.hdpe.pushfight.server.web.swagger

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.swagger.schema.ApiModelTypeNameProvider

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TypeNameEnhancingSwaggerPlugin(val delegateTypeNameProvider: ApiModelTypeNameProvider) : TypeNameProviderPlugin {
    override fun nameFor(type: Class<*>?): String {
        return if (type.toString().endsWith("WebGame")) "Game" else delegateTypeNameProvider.nameFor(type)
    }

    override fun supports(delimiter: DocumentationType): Boolean = (delimiter == DocumentationType.SWAGGER_2)

}