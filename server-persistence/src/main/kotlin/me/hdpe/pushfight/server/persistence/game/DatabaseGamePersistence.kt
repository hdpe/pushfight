package me.hdpe.pushfight.server.persistence.game

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class DatabaseGamePersistence(val repository: WebGameRepository, val mapper: StateMapper) : AbstractGamePersistence() {

    override fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                            gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame {

        val players = Pair(createWebPlayer(1, createPlayerCommands.first), createWebPlayer(2, createPlayerCommands.second))

        val game = createWebGame(players, gameStateCreator)

        repository.save(WebGameEntity(game.id, game.player1AccountId, game.player2AccountId,
                mapper.serialize(game.gameState), game.victorAccountId))

        return game
    }

    override fun getGame(id: String): WebGame {
        val entity = repository.findById(id).orElseThrow { createNoSuchGameException(id) }

        return toWebGame(entity)
    }

    override fun updateGame(id: String, gameState: GameState): WebGame {
        val entity = repository.findById(id).orElseThrow { createNoSuchGameException(id) }

        entity.state = mapper.serialize(gameState)
        entity.victorAccountId = gameState.result?.victor?.number?.let {
            if (it == 1) entity.player1AccountId else entity.player2AccountId
        }

        return toWebGame(entity)
    }

    override fun getActiveGames(accountId: String): List<WebGame> {
        return repository.findAllByAccountIdAndNotVictorOrModifiedSince(accountId, LocalDateTime.now().minusDays(7)).map { toWebGame(it) }
    }

    private fun toWebGame(entity: WebGameEntity) =
            WebGame(entity.id, entity.player1AccountId, entity.player2AccountId, mapper.deserialize(entity.state),
                    entity.victorAccountId, ZonedDateTime.of(entity.lastModified, ZoneId.systemDefault()))
}