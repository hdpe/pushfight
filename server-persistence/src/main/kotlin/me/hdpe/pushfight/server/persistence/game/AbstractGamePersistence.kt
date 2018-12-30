package me.hdpe.pushfight.server.persistence.game

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import java.util.*

abstract class AbstractGamePersistence : GamePersistence {

    protected fun createNoSuchGameException(id: String) = NoSuchGameException("no such game $id")

    protected fun createWebPlayer(number: Int, command: CreatePlayerCommand) =
            WebPlayer(number, command.accountId, command.playerName)

    protected fun createWebGame(players: Pair<WebPlayer, WebPlayer>,
                                gameStateCreator: (Pair<Player, Player>) -> GameState): WebGame {
        return WebGame(UUID.randomUUID().toString(), players.first.accountId, players.second.accountId,
                gameStateCreator(players), null)
    }
}