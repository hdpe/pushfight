package me.hdpe.pushfight.server.web.util

import me.hdpe.pushfight.server.web.accounts.AccountDetails
import me.hdpe.pushfight.server.web.accounts.AccountDetailsProvider
import me.hdpe.pushfight.server.web.game.BadPlayRequestException
import me.hdpe.pushfight.server.web.security.ClientDetails

fun accountFromIdOrPrincipal(accountDetailsProvider: AccountDetailsProvider, accountId: String?,
                                     principal: ClientDetails): AccountDetails {
    val finalAccountId: String = when(accountId) {
        null -> principal.fixedAccountId ?: throw BadPlayRequestException("no fixed account id for client " +
                "${principal.id} - need to explicitly provide account")
        else -> accountId
    }

    return accountFromId(accountDetailsProvider, finalAccountId)
}

fun accountFromId(accountDetailsProvider: AccountDetailsProvider, accountId: String): AccountDetails {
    return accountDetailsProvider.accounts.find { it.id == accountId }
            ?: throw BadPlayRequestException("no such account '$accountId'")
}
