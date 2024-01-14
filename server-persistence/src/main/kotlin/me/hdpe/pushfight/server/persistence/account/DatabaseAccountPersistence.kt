package me.hdpe.pushfight.server.persistence.account

import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class DatabaseAccountPersistence(val repository: AccountRepository,
                                 val jdbcRepository: AccountJdbcRepository) : AccountPersistence {

    override fun getAccount(username: String): AccountDetails? = repository.findByUsername(username)
            .map { toAccountDetails(it) }.getOrNull()

    override fun getAccountById(id: String): AccountDetails? = repository.findById(id)
            .map { toAccountDetails(it) }.getOrNull()

    override fun getActiveAccounts(): List<AccountWithStatisticsDetails> = jdbcRepository.findAllWithStatistics().toList()

    override fun createAccount(command: CreateAccountCommand): AccountDetails {
        val account = AccountEntity(UUID.randomUUID().toString(), command.username)
        repository.save(account)
        return toAccountDetails(account)
    }

    private fun toAccountDetails(entity: AccountEntity) = AccountDetails(entity.id, entity.username)
}
