package me.hdpe.pushfight.server.web.accounts

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.persistence.account.NoSuchAccountException
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithContentApiResponsesAndConflict
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.ClientDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@Api(tags = ["Accounts"])
@RequestMapping("/account")
class AccountController(val service: AccountService) {

    @PostMapping
    @ApiOperation(value = "Create Account", nickname = "createAccount")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithContentApiResponsesAndConflict
    fun create(@AuthenticationPrincipal principal: ClientDetails,
               @RequestBody request: CreateAccountRequest): AccountResult {
        return service.create(principal, request);
    }

    @GetMapping
    @ApiOperation(value = "Get Account", nickname = "getAccount")
    @AuthorizationHeaderRequired
    @AuthenticationAndAccountFoundRequiredRequestWithNoContentApiResponses
    fun account(@RequestParam("username") username: String): AccountWithStatisticsResult {
        return service.getActiveAccounts().firstOrNull { it.name == username } ?: throw NoSuchAccountException(username);
    }

    @ExceptionHandler(AccountCreationForbiddenException::class)
    fun handleAccountCreationForbiddenException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @ExceptionHandler(AccountAlreadyExistsException::class)
    fun handleAccountAlreadyExistsException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_CONFLICT);
    }

    @ExceptionHandler(NoSuchAccountException::class)
    fun handleNoSuchAccountException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}