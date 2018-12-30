package me.hdpe.pushfight.server.web

import me.hdpe.pushfight.server.web.security.SecurityProperties
import me.hdpe.pushfight.server.web.security.env.EnvironmentClientDetailsProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnvironmentConfig {

    @Bean
    fun clientDetailsProvider(securityProperties: SecurityProperties) = EnvironmentClientDetailsProvider(securityProperties);
}