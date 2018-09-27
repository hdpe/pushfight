package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import java.util.*

class InMemoryPersistenceService : PersistenceService {

    private val games: MutableMap<String, WebGame> = mutableMapOf()

    override fun createGame(playerAccountIds: Pair<String, String>,
                            gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame {
        val players = Pair(createWebPlayer(playerAccountIds.first), createWebPlayer(playerAccountIds.second))

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

        val updated = WebGame(id, gameState)
        games[id] = updated
        return updated
    }

    private fun createNoSuchGameException(id: String) = NoSuchGameException("no such game $id")

    private fun createWebPlayer(accountId: String) = WebPlayer(UUID.randomUUID().toString(), accountId)

    private fun createWebGame(players: Pair<WebPlayer, WebPlayer>,
                              gameStateCreator: (Pair<Player, Player>) -> GameState): WebGame {
        return WebGame(UUID.randomUUID().toString(), gameStateCreator(players))
    }
}