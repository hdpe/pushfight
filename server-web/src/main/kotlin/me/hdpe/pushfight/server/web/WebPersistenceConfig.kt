package me.hdpe.pushfight.server.web

import me.hdpe.pushfight.server.persistence.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebPersistenceConfig {

    @Bean
    fun webPersistence(repository: WebGameRepository, mapper: StateMapper): PersistenceService =
            DatabasePersistenceService(repository, mapper)
}