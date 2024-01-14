package me.hdpe.pushfight.server.persistence

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
class PersistenceConfig {
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource);
    }
}