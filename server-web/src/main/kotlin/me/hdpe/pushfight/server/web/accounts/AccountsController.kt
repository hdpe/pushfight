package me.hdpe.pushfight.server.web.accounts

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.util.accountFromIdOrPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Accounts"])
class AccountsController(val accountDetailsProvider: AccountDetailsProvider) {

    @GetMapping("/accounts")
    @ApiOperation(value = "Get Accounts", nickname = "accounts")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithNoContentApiResponses
    fun accounts(@AuthenticationPrincipal principal: ClientDetails,
                 @RequestParam(value = "accountId", required = false) accountId: String?): List<AccountResult> {
        val account = accountFromIdOrPrincipal(accountDetailsProvider, accountId, principal)

        return accountDetailsProvider.accounts.asSequence()
                .filter { it.id != account.id }.map { AccountResult(it.id, it.name) }.toList() }
}