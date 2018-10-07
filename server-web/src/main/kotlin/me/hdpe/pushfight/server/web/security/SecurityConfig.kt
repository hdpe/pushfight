package me.hdpe.pushfight.server.web.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@ComponentScan
class SecurityConfig {

    @Bean
    @ConfigurationProperties("pushfight.security")
    fun securityProperties() = SecurityProperties()

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}