package me.hdpe.pushfight.server.web.notifications

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
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
    @ConditionalOnProperty("pushfight.notifications.sqs-url")
    fun sqsClient(): SqsClient = SqsClient.builder().build();

    @Bean
    @ConditionalOnBean(SqsClient::class)
    fun notificationsClient(sqsClient: SqsClient,
                            notificationsProperties: NotificationsProperties): DefaultNotificationsClient =
            DefaultNotificationsClient(sqsClient, notificationsProperties)

    @Bean
    @ConditionalOnMissingBean(SqsClient::class)
    fun nullNotificationsClient() = NullNotificationsClient()
}