package me.hdpe.pushfight.server.web.accounts

import me.hdpe.pushfight.server.persistence.PersistenceService
import me.hdpe.pushfight.server.persistence.account.*
import me.hdpe.pushfight.server.persistence.util.isConstraintViolation
import me.hdpe.pushfight.server.web.security.ClientDetails
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

@Service
class AccountService(private val persistenceService: PersistenceService) {
    fun create(principal: ClientDetails, request: CreateAccountRequest): AccountResult {
        if (principal.fixedAccountId != null) {
            throw AccountCreationForbiddenException()
        }

        val command = CreateAccountCommand(request.username!!)

        val details: AccountDetails
        try {
            details = persistenceService.createAccount(command)
        } catch (exception: DataAccessException) {
            if (isConstraintViolation(exception, USERNAME_UNIQUE_CONSTRAINT_NAME)) {
                throw AccountAlreadyExistsException()
            }

            throw exception
        }

        return toAccountResult(details)
    }

    fun get(username: String): AccountResult = persistenceService.getAccount(username)?.let { toAccountResult(it) }
            ?: throw NoSuchAccountException(username)

    fun getById(id: String): AccountResult? = persistenceService.getAccountById(id)?.let { toAccountResult(it) }

    fun getActiveAccounts(): List<AccountWithStatisticsResult> = persistenceService.getActiveAccounts()
            .map { toAccountWithStatisticsResult(it) }

    private fun toAccountResult(details: AccountDetails): AccountResult = AccountResult(details.id, details.name)

    private fun toAccountWithStatisticsResult(details: AccountWithStatisticsDetails): AccountWithStatisticsResult =
            AccountWithStatisticsResult(details.id, details.name, details.played, details.won, details.lost)
}