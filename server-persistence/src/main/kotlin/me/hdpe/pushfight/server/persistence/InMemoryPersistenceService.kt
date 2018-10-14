package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import java.util.*

class InMemoryPersistenceService : AbstractPersistenceService() {

    private val games: MutableMap<String, WebGame> = mutableMapOf()

    override fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                            gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame {
        val players = Pair(createWebPlayer(1, createPlayerCommands.first), createWebPlayer(2, createPlayerCommands.second))

        val game = createWebGame(players, gameStateCreator)

        games[game.id] = game

        return game
    }

    override fun getGame(id: String): WebGame {
        return games[id] ?: throw createNoSuchGameException(id)
    }

    override fun updateGame(id: String, gameState: GameState): WebGame {
        if (!games.containsKey(id)) {
            throw createNoSuchGameException(id)
        }

        val existing = games[id]!!

        val updated = WebGame(id, existing.player1AccountId, existing.player2AccountId, gameState,
                existing.victorAccountId)
        games[id] = updated
        return updated
    }

    override fun getActiveGames(accountId: String): List<WebGame> {
        return games.values.filter { arrayOf(it.player1AccountId, it.player2AccountId).contains(accountId) &&
                it.victorAccountId == null }
    }
}