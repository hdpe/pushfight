package me.hdpe.pushfight.server.web

import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class WriteConfig {

    @Bean
    fun accountDetailsProvider(): AccountDetailsProvider {
        val provider = Mockito.mock(AccountDetailsProvider::class.java)
        Mockito.`when`(provider.accounts).thenReturn(listOf(
                AccountDetails("1000", "You", "testAccessKeyId", "s3CrEt"),
                AccountDetails("1001", "Adversary 1", "testAccessKeyId2", "s3CrEt2"),
                AccountDetails("1002", "Adversary 2", "_", "_")
        ))
        return provider
    }
}