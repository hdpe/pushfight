package me.hdpe.pushfight.server.web.game

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.ClientDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
@Api(tags = ["Game"])
class GamesController(val service: GameService) {

    @GetMapping("/active")
    @ApiOperation(value = "Get Active Games", nickname = "getActiveGames")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithNoContentApiResponses
    fun getActiveGames(@AuthenticationPrincipal principal: ClientDetails,
                       @RequestParam("accountId") accountId: String): List<GameSummary> {
        return service.getActiveGames(principal, accountId)
    }
}