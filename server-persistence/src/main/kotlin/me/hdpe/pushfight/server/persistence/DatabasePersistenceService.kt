package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.GameState
import me.hdpe.pushfight.engine.Player
import me.hdpe.pushfight.server.persistence.account.AccountDetails
import me.hdpe.pushfight.server.persistence.account.AccountPersistence
import me.hdpe.pushfight.server.persistence.account.CreateAccountCommand
import me.hdpe.pushfight.server.persistence.game.CreatePlayerCommand
import me.hdpe.pushfight.server.persistence.game.GamePersistence
import me.hdpe.pushfight.server.persistence.game.WebGame
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DatabasePersistenceService(val gamePersistence: GamePersistence, val accountPersistence: AccountPersistence) :
        PersistenceService {

    @Transactional(readOnly = true)
    override fun getAccount(username: String): AccountDetails = accountPersistence.getAccount(username)

    @Transactional
    override fun createGame(createPlayerCommands: Pair<CreatePlayerCommand, CreatePlayerCommand>,
                            gameStateCreator: (players: Pair<Player, Player>) -> GameState): WebGame =
            gamePersistence.createGame(createPlayerCommands, gameStateCreator)

    @Transactional(readOnly = true)
    override fun getGame(id: String): WebGame = gamePersistence.getGame(id)

    @Transactional
    override fun updateGame(id: String, gameState: GameState): WebGame = gamePersistence.updateGame(id, gameState)

    @Transactional(readOnly = true)
    override fun getActiveGames(accountId: String): List<WebGame> = gamePersistence.getActiveGames(accountId)

    @Transactional
    override fun createAccount(command: CreateAccountCommand): AccountDetails = accountPersistence.createAccount(command)

    @Transactional(readOnly = true)
    override fun getActiveAccounts(): List<AccountDetails> = accountPersistence.getActiveAccounts()
}