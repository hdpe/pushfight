package me.hdpe.pushfight.server.web.accounts

import io.swagger.annotations.*
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.AccountDetails
import me.hdpe.pushfight.server.web.security.AccountDetailsProvider
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Accounts"])
class AccountsController(val accountDetailsProvider: AccountDetailsProvider) {

    @GetMapping("/accounts")
    @ApiOperation(value = "Get Accounts", nickname = "accounts")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithNoContentApiResponses
    fun accounts(@AuthenticationPrincipal principal: AccountDetails): List<AccountResult> =
            accountDetailsProvider.accounts.asSequence()
                    .filter { it.id != principal.id }.map { AccountResult(it.id, it.name) }.toList()
}