package me.hdpe.pushfight.server.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import me.hdpe.pushfight.engine.GameState

class WebGame(
        val id: String,

        @get:JsonIgnore val player1AccountId: String,

        @get:JsonIgnore val player2AccountId: String,

        val gameState: GameState,

        @get:JsonIgnore val victorAccountId: String?
) {
    fun getPlayer(number: Int): WebPlayer {
        return with(gameState.config) { if (number == 1) player1 else player2 } as WebPlayer
    }
}
