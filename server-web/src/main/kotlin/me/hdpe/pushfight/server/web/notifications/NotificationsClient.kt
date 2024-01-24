package me.hdpe.pushfight.server.web.notifications

interface NotificationsClient {
    fun notify(accountId: String, notificationType: NotificationType, gameId: String, opponentName: String)
}