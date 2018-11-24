package me.hdpe.pushfight.server.web

import me.hdpe.pushfight.server.web.accounts.AccountProperties
import me.hdpe.pushfight.server.web.accounts.env.EnvironmentAccountDetailsProvider
import me.hdpe.pushfight.server.web.security.SecurityProperties
import me.hdpe.pushfight.server.web.security.env.EnvironmentClientDetailsProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnvironmentConfig {

    @Bean
    fun accountDetailsProvider() = EnvironmentAccountDetailsProvider(accountProperties())

    @Bean
    fun clientDetailsProvider(securityProperties: SecurityProperties) = EnvironmentClientDetailsProvider(securityProperties);

    @Bean
    @ConfigurationProperties("pushfight.accounts")
    fun accountProperties() = AccountProperties()
}