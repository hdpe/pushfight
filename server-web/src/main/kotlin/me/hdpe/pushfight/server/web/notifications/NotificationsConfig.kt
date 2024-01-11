package me.hdpe.pushfight.server.web.notifications

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class NotificationsConfig {
    @Bean
    @ConfigurationProperties("pushfight.notifications")
    fun notificationsProperties() = NotificationsProperties()

    @Bean
    fun sqsClient(): SqsClient = SqsClient.builder().build();
}