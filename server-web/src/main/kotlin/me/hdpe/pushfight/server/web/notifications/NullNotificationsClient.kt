package me.hdpe.pushfight.server.web.notifications

class NullNotificationsClient : NotificationsClient {
    override fun notify(accountId: String, notificationType: NotificationType, gameId: String, opponentName: String) {
    }
}