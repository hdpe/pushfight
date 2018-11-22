package me.hdpe.pushfight.server.web

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import me.hdpe.pushfight.server.persistence.InMemoryPersistenceService
import me.hdpe.pushfight.server.persistence.PersistenceService
import me.hdpe.pushfight.server.web.accounts.AccountDetails
import me.hdpe.pushfight.server.web.accounts.AccountDetailsProvider
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.security.ClientDetailsProvider
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
                    AccountDetails("100", "You"),
                    AccountDetails("101", "Adversary 1"),
                    AccountDetails("102", "Adversary 2")
            )
        }
    }

    @Bean
    fun clientDetailsProvider(): ClientDetailsProvider {
        return mock {
            on { clients } doReturn listOf(
                    ClientDetails("1000", "You", "testAccessKeyId", "s3CrEt", "100"),
                    ClientDetails("1001", "Adversary 1", "testAccessKeyId2", "s3CrEt2", "101"),
                    ClientDetails("1002", "Adversary 2", "_", "_", "102")
            )
        }
    }

    @Bean
    fun webPersistence(): PersistenceService = InMemoryPersistenceService()
}