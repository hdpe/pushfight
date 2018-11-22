package me.hdpe.pushfight.server.web.accounts

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class AccountsConfig {

    @Bean
    @ConfigurationProperties("pushfight.accounts")
    fun accountProperties() = AccountProperties()

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}