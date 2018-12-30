package me.hdpe.pushfight.server.persistence.account

import org.springframework.stereotype.Service
import java.util.*

@Service
class DatabaseAccountPersistence(val repository: AccountRepository) : AccountPersistence {

    override fun getAccount(username: String): AccountDetails = repository.findByUsername(username)
            .map { toAccountDetails(it) }
            .orElseThrow { NoSuchAccountException(username) };

    override fun getActiveAccounts(): List<AccountDetails> = repository.findAll()
            .map { toAccountDetails(it) }

    override fun createAccount(command: CreateAccountCommand): AccountDetails {
        val account = AccountEntity(UUID.randomUUID().toString(), command.username)
        repository.save(account)
        return toAccountDetails(account)
    }

    private fun toAccountDetails(entity: AccountEntity) = AccountDetails(entity.id, entity.username)
}