package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.KotlinModule
import me.hdpe.pushfight.server.web.accounts.AccountsConfig
import me.hdpe.pushfight.server.web.game.GameConfig
import me.hdpe.pushfight.server.web.security.SecurityConfig
import me.hdpe.pushfight.server.web.token.TokenConfig
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    AccountsConfig::class,
    GameConfig::class,
    SecurityConfig::class,
    TokenConfig::class,
    WebMvcConfig::class,
    WebSwaggerConfig::class
)
class WebConfig {

    @Bean
    fun errorAttributes(): DefaultErrorAttributes = ReducedErrorAttributes()

    @Bean
    fun objectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
            Jackson2ObjectMapperBuilderCustomizer { builder ->
                builder.modules(WebJacksonModule(), KotlinModule())
                        .serializationInclusion(JsonInclude.Include.NON_NULL) }
}