package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
class DatabasePersistenceService(val repository: WebGameRepository, val mapper: StateMapper) : PersistenceService {

    override fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                            gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame {

        val players = Pair(createWebPlayer(1, createPlayerCommands.first), createWebPlayer(2, createPlayerCommands.second))

        val game = createWebGame(players, gameStateCreator)

        repository.save(WebGameEntity(game.id, mapper.serialize(game.gameState)))

        return game
    }

    @Transactional(readOnly = true)
    override fun getGame(id: String): WebGame {
        val entity = repository.findById(id).orElseThrow { createNoSuchGameException(id) }

        return WebGame(entity.id, mapper.deserialize(entity.state))
    }

    override fun updateGame(id: String, gameState: GameState): WebGame {
        val entity = repository.findById(id).orElseThrow { createNoSuchGameException(id) }

        entity.state = mapper.serialize(gameState)

        return WebGame(entity.id, gameState)
    }

    private fun createNoSuchGameException(id: String) = NoSuchGameException("no such game $id")

    private fun createWebPlayer(number: Int, command: CreatePlayerCommand) =
            WebPlayer(number, command.accountId, command.playerName)

    private fun createWebGame(players: Pair<WebPlayer, WebPlayer>,
                              gameStateCreator: (Pair<Player, Player>) -> GameState): WebGame {
        return WebGame(UUID.randomUUID().toString(), gameStateCreator(players))
    }
}