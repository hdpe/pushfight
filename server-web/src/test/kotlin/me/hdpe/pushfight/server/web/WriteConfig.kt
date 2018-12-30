package me.hdpe.pushfight.server.web

import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.security.ClientDetailsProvider
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class WriteConfig {

    @Bean
    fun writeState(): WriteState {
        return WriteState()
    }

    @Bean
    fun clientDetailsProvider(writeState: WriteState): ClientDetailsProvider {
        return mock {
            on { clients } doAnswer {
                listOf(
                        ClientDetails("10000", "You API", "testAccessKeyId", "s3CrEt", writeState.accountIdsByName[ExampleAccountNames.YOU]),
                        ClientDetails("10001", "Adversary 1 API", "testAccessKeyId2", "s3CrEt2")
                )
            }
        }
    }
}