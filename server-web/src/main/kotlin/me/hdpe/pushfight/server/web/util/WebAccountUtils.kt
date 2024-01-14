package me.hdpe.pushfight.server.web.util

import me.hdpe.pushfight.server.web.accounts.AccountResult
import me.hdpe.pushfight.server.web.accounts.AccountService
import me.hdpe.pushfight.server.web.game.BadPlayRequestException
import me.hdpe.pushfight.server.web.security.ClientDetails

fun accountFromIdOrPrincipal(accountService: AccountService, accountId: String?,
                             principal: ClientDetails): AccountResult {
    val finalAccountId: String = when(accountId) {
        null -> principal.fixedAccountId ?: throw BadPlayRequestException("no fixed account id for client " +
                "${principal.id} - need to explicitly provide account")
        else -> accountId
    }

    return accountFromId(accountService, finalAccountId)
}

fun accountFromId(accountService: AccountService, accountId: String): AccountResult {
    return accountService.getById(accountId)
            ?: throw BadPlayRequestException("no such account '$accountId'")
}
