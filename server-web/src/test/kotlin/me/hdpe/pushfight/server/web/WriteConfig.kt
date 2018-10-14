package me.hdpe.pushfight.server.web

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import me.hdpe.pushfight.server.persistence.InMemoryPersistenceService
import me.hdpe.pushfight.server.persistence.PersistenceService
import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class WriteConfig {

    @Bean
    fun accountDetailsProvider(): AccountDetailsProvider {
        return mock {
            on { accounts } doReturn listOf(
                    AccountDetails("1000", "You", "testAccessKeyId", "s3CrEt"),
                    AccountDetails("1001", "Adversary 1", "testAccessKeyId2", "s3CrEt2"),
                    AccountDetails("1002", "Adversary 2", "_", "_")
            )
        }
    }

    @Bean
    fun webPersistence(): PersistenceService = InMemoryPersistenceService()
}