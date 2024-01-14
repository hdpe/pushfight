package me.hdpe.pushfight.server.web.accounts

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.ClientDetails
import me.hdpe.pushfight.server.web.util.accountFromIdOrPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Accounts"])
@RequestMapping("/accounts/opponents")
class AccountsController(private val service: AccountService) {

    @GetMapping
    @ApiOperation(value = "Get Available Opponents", nickname = "getAvailableOpponents")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithNoContentApiResponses
    fun accounts(@AuthenticationPrincipal principal: ClientDetails,
                 @RequestParam(value = "accountId", required = false) accountId: String?): List<AccountWithStatisticsResult> {
        val account = accountFromIdOrPrincipal(service, accountId, principal)

        return service.getActiveAccounts().asSequence().filter { it.id != account.id }.toList()
    }
}