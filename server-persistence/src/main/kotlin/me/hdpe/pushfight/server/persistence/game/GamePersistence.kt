package me.hdpe.pushfight.server.persistence.game

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player

interface GamePersistence {

    fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                   gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame

    fun getGame(id: String): WebGame

    fun updateGame(id: String, gameState: GameState): WebGame

    fun getActiveGames(accountId: String): List<WebGame>
}
