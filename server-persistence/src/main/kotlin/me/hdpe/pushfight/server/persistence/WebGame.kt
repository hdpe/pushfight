package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState

class WebGame(val id: String, val gameState: GameState) {

    fun getPlayer(number: Int): WebPlayer {
        return with(gameState.config) { if (number == 1) player1 else player2 } as WebPlayer
    }
}
