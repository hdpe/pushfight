package me.hdpe.pushfight.server.web.notifications

class SendNotificationData(
        val type: NotificationType,
        val apiAccountId: String,
        val gameId: String,
        val opponentName: String
)