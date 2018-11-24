package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.GameStateFactory
import me.hdpe.pushfight.engine.Player
import me.hdpe.pushfight.engine.command.InitialPlacementCommand
import me.hdpe.pushfight.engine.command.UpdatedPlacementCommand
import me.hdpe.pushfight.server.persistence.CreatePlayerCommand
import me.hdpe.pushfight.server.persistence.PersistenceService
import me.hdpe.pushfight.server.persistence.WebGame
import me.hdpe.pushfight.server.web.accounts.AccountDetailsProvider
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.util.accountFromId
import me.hdpe.pushfight.server.web.util.accountFromIdOrPrincipal
import org.springframework.stereotype.Service

@Service
class GameService(val gameStateFactory: GameStateFactory, val accountDetailsProvider: AccountDetailsProvider,
                  val persistenceService: PersistenceService) {

    fun createGame(principal: ClientDetails, accountId: String?, opponentId: String): WebGame {
        val aggressor = accountFromIdOrPrincipal(accountDetailsProvider, accountId, principal)

        val opponent = accountFromId(accountDetailsProvider, opponentId)

        val (principalAccountId, opponentAccountId) = arrayOf(aggressor, opponent).map {
            CreatePlayerCommand(it.id, it.name) }

        return persistenceService.createGame(Pair(principalAccountId, opponentAccountId)) { (player1, player2) ->
            gameStateFactory.create(me.hdpe.pushfight.engine.GameConfig(player1, player2)) }
    }

    fun getGame(principal: ClientDetails, gameId: String): WebGame {
        return persistenceService.getGame(gameId)
    }

    fun putInitialPlacements(principal: ClientDetails, gameId: String, playerNumber: Int,
                             placements: List<InitialPlacement>): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withInitialPlacements(player, placements.map { toEngineCommand(it) }) }
    }

    fun putUpdatedPlacements(principal: ClientDetails, gameId: String, playerNumber: Int,
                             placements: List<UpdatedPlacement>): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withUpdatedPlacements(player, placements.map { toEngineCommand(it) }) }
    }

    fun putSetupConfirmation(principal: ClientDetails, gameId: String, playerNumber: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player -> state.withSetupConfirmed(player) }
    }

    fun putMove(principal: ClientDetails, gameId: String, playerNumber: Int, startX: Int, startY: Int, endX: Int,
                endY: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withMove(player, startX, startY, endX, endY) }
    }

    fun putPush(principal: ClientDetails, gameId: String, playerNumber: Int, startX: Int, startY: Int, endX: Int,
                endY: Int): WebGame {
        return updateGame(principal, gameId, playerNumber) { state, player ->
            state.withPush(player, startX, startY, endX, endY) }
    }

    fun getActiveGames(principal: ClientDetails, accountId: String?): List<GameSummary> {
        val account = accountFromIdOrPrincipal(accountDetailsProvider, accountId, principal)

        return persistenceService.getActiveGames(account.id).map { game ->
            val opponent = when (accountId) {
                game.player1AccountId -> game.player2AccountId
                else -> game.player1AccountId
            }

            GameSummary(game.id, accountDetailsProvider.accounts.find { it.id == opponent }!!.name)
        }
    }

    private fun updateGame(principal: ClientDetails, gameId: String,
                           playerNumber: Int,
                           updateAction: (currentState: GameState, player: Player) -> GameState): WebGame {
        val game = persistenceService.getGame(gameId)

        val player = game.getPlayer(playerNumber)

        if (principal.fixedAccountId != null && player.accountId != principal.fixedAccountId) {
            throw PlayForbiddenException("not your player to play")
        }

        return persistenceService.updateGame(gameId, updateAction(game.gameState, player))
    }

    private fun toEngineCommand(placement: InitialPlacement) =
            InitialPlacementCommand(placement.pieceType!!, placement.x!!, placement.y!!)

    private fun toEngineCommand(placement: UpdatedPlacement) =
            UpdatedPlacementCommand(placement.currentX!!, placement.currentY!!, placement.newX!!, placement.newY!!)
}