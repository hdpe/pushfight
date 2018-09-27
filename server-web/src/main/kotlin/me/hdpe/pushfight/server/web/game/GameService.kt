package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.GameStateFactory
import me.hdpe.pushfight.engine.Player
import me.hdpe.pushfight.engine.command.InitialPlacementCommand
import me.hdpe.pushfight.engine.command.UpdatedPlacementCommand
import me.hdpe.pushfight.server.persistence.PersistenceService
import me.hdpe.pushfight.server.persistence.WebGame
import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import org.springframework.stereotype.Service

@Service
class GameService(val gameStateFactory: GameStateFactory, val accountDetailsProvider: AccountDetailsProvider,
                  val persistenceService: PersistenceService) {

    fun createGame(principal: AccountDetails, opponentId: String): WebGame {
        val opponent = accountDetailsProvider.accounts.find { it.id == opponentId } ?:
                throw BadPlayRequestException("no such account '$opponentId'")

        val (principalAccountId, opponentAccountId) = arrayOf(principal, opponent).map { it.id }

        return persistenceService.createGame(Pair(principalAccountId, opponentAccountId)) { (player1, player2) ->
            gameStateFactory.create(me.hdpe.pushfight.engine.GameConfig(player1, player2)) }
    }

    fun putInitialPlacements(principal: AccountDetails, gameId: String, playerNumber: Int,
                             placements: List<InitialPlacement>): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withInitialPlacements(player, placements.map { toEngineCommand(it) }) }
    }

    fun putUpdatedPlacements(principal: AccountDetails, gameId: String, playerNumber: Int,
                             placements: List<UpdatedPlacement>): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withUpdatedPlacements(player, placements.map { toEngineCommand(it) }) }
    }

    fun putSetupConfirmation(principal: AccountDetails, gameId: String, playerNumber: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player -> state.withSetupConfirmed(player) }
    }

    fun putMove(principal: AccountDetails, gameId: String, playerNumber: Int, startX: Int, startY: Int, endX: Int,
                endY: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withMove(player, startX, startY, endX, endY) }
    }

    fun putPush(principal: AccountDetails, gameId: String, playerNumber: Int, startX: Int, startY: Int, endX: Int,
                endY: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withPush(player, startX, startY, endX, endY) }
    }

    private fun updateGame(principal: AccountDetails, gameId: String,
                           playerNumber: Int,
                           updateAction: (currentState: GameState, player: Player) -> GameState): WebGame {
        val game = persistenceService.getGame(gameId)

        val player = game.getPlayer(playerNumber)

        if (player.accountId != principal.id) {
            throw PlayForbiddenException("not your player to play")
        }

        return persistenceService.updateGame(gameId, updateAction(game.gameState, player))
    }

    private fun toEngineCommand(placement: InitialPlacement) =
            InitialPlacementCommand(placement.pieceType!!, placement.x!!, placement.y!!)

    private fun toEngineCommand(placement: UpdatedPlacement) =
            UpdatedPlacementCommand(placement.currentX!!, placement.currentY!!, placement.newX!!, placement.newY!!)
}