package me.hdpe.pushfight.server.persistence

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ComponentScan
@EnableJpaRepositories
@EntityScan
class PersistenceConfig {
}