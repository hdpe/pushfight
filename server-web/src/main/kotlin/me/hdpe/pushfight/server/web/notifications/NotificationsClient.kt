package me.hdpe.pushfight.server.web.notifications

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Component
class NotificationsClient(val sqsClient: SqsClient, val notificationsProperties: NotificationsProperties) {
    private final val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    fun notify(accountId: String, notificationType: NotificationType, gameId: String, opponentName: String) {
        val message = NotificationsAction(
            "send_notification",
                SendNotificationData(notificationType, accountId, gameId, opponentName)
        )

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(notificationsProperties.sqsUrl)
                .messageBody(mapper.writeValueAsString(message))
                .build())
    }
}