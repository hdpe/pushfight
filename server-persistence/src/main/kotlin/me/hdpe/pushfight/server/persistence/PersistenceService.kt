package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player

interface PersistenceService {

    fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                   gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame

    fun getGame(id: String): WebGame

    fun updateGame(id: String, gameState: GameState): WebGame

    fun getActiveGames(accountId: String): List<WebGame>
}
