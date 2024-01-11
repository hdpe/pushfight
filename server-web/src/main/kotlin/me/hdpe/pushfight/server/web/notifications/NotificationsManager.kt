package me.hdpe.pushfight.server.web.notifications

import me.hdpe.pushfight.server.persistence.game.WebGame
import me.hdpe.pushfight.server.persistence.game.WebPlayer
import org.springframework.stereotype.Component

@Component
class NotificationsManager(val client: NotificationsClient) {
    fun notifySetupComplete(playerNumber: Int, game: WebGame) {
        val state = game.gameState
        val setup = state.setup

        if (setup.isComplete()) {
            if (playerNumber != state.turn.player.number) {
                notifyTurn(state.turn.player.number, game)
            }
        } else {
            val next = nextPlayerNumber(playerNumber)
            val playerSetup = if (next == 1) setup.player1Setup else setup.player2Setup

            if (!playerSetup.complete) {
                notifyDoSetup(next, game)
            }
        }
    }

    fun notifyMoveEnd(playerNumber: Int, game: WebGame) {
        val state = game.gameState

        if (state.result == null) {
            notifyTurn(state.turn.player.number, game)
        } else {
            notifyGameOver(nextPlayerNumber(playerNumber), game)
        }
    }

    private fun notifyDoSetup(recipientPlayer: Int, game: WebGame) {
        notify(NotificationType.finish_setup, recipientPlayer, game)
    }

    private fun notifyTurn(recipientPlayer: Int, game: WebGame) {
        notify(NotificationType.your_turn, recipientPlayer, game)
    }

    private fun notifyGameOver(recipientPlayer: Int, game: WebGame) {
        notify(NotificationType.game_over, recipientPlayer, game)
    }

    private fun notify(type: NotificationType, recipientPlayer: Int, game: WebGame) {
        val accountId = if (recipientPlayer == 1) game.player1AccountId else game.player2AccountId
        val config = game.gameState.config
        val opponent = (if (recipientPlayer == 1) config.player2 else config.player1) as WebPlayer

        client.notify(accountId, type, game.id, opponent.playerName)
    }

    private fun nextPlayerNumber(playerNumber: Int): Int {
        return if (playerNumber == 1) 2 else 1
    }
}