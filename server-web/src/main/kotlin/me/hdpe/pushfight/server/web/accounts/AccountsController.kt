package me.hdpe.pushfight.server.web.accounts

import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountsController(val accountDetailsProvider: AccountDetailsProvider) {

    @GetMapping("/accounts")
    fun accounts(@AuthenticationPrincipal principal: AccountDetails): List<AccountResult> =
            accountDetailsProvider.accounts.asSequence()
                    .filter { it.id != principal.id }.map { AccountResult(it.id, it.name) }.toList()
}