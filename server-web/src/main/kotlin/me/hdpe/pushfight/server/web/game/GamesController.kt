package me.hdpe.pushfight.server.web.game

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.AccountDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
@Api(tags = ["Game"])
class GamesController(val service: GameService) {

    @GetMapping("/active")
    @ApiOperation(value = "Get Active Games", nickname = "getActiveGames")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithNoContentApiResponses
    fun getActiveGames(@AuthenticationPrincipal principal: AccountDetails): List<GameSummary> {
        return service.getActiveGames(principal)
    }
}