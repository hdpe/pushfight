package me.hdpe.pushfight.server.web.game

import io.swagger.annotations.*
import me.hdpe.pushfight.engine.IllegalEventException
import me.hdpe.pushfight.server.persistence.NoSuchGameException
import me.hdpe.pushfight.server.persistence.WebGame
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithContentApiResponses
import me.hdpe.pushfight.server.web.AuthenticationRequiredRequestWithNoContentApiResponses
import me.hdpe.pushfight.server.web.AuthorizationHeaderRequired
import me.hdpe.pushfight.server.web.security.AccountDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/game")
@Api(tags = ["Game"])
class GameController(val service: GameService) {

    @PostMapping
    @ApiOperation(value = "Create Game", nickname = "createGame")
    @AuthorizationHeaderRequired
    @AuthenticationRequiredRequestWithContentApiResponses
    fun create(@AuthenticationPrincipal principal: AccountDetails,
               @Valid @RequestBody request: CreateGameRequest): WebGame {
        return service.createGame(principal, request.opponent!!)
    }

    @GetMapping("/{gameId}")
    @ApiOperation(value = "Get Game", nickname = "getGame")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithNoContentApiResponses
    fun getGame(@AuthenticationPrincipal principal: AccountDetails,
                @PathVariable("gameId") gameId: String): WebGame {
        return service.getGame(principal, gameId)
    }

    @PostMapping("/{gameId}/setup")
    @ApiOperation(value = "Put Initial Placements", nickname = "initialPlacements")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithContentApiResponses
    fun initialPlacements(@AuthenticationPrincipal principal: AccountDetails,
                          @PathVariable("gameId") gameId: String,
                          @Valid @RequestBody request: InitialPlacementsRequest): WebGame {
        return service.putInitialPlacements(principal, gameId, request.playerNumber!!, request.placements!!.asList())
    }

    @PatchMapping("/{gameId}/setup")
    @ApiOperation(value = "Update Initial Placements", nickname = "updatePlacements")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithContentApiResponses
    fun updatePlacements(@AuthenticationPrincipal principal: AccountDetails,
                         @PathVariable("gameId") gameId: String,
                         @Valid @RequestBody request: UpdatePlacementsRequest): WebGame {
        return service.putUpdatedPlacements(principal, gameId, request.playerNumber!!, request.placements!!.asList())
    }

    @PostMapping("/{gameId}/setup/confirm")
    @ApiOperation(value = "Confirm Setup", nickname = "confirmSetup")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithContentApiResponses
    fun confirmSetup(@AuthenticationPrincipal principal: AccountDetails,
                     @PathVariable("gameId") gameId: String,
                     @Valid @RequestBody request: ConfirmSetupRequest): WebGame {
        return service.putSetupConfirmation(principal, gameId, request.playerNumber!!)
    }

    @PostMapping("/{gameId}/turn/move")
    @ApiOperation(value = "Perform Move", nickname = "move")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithContentApiResponses
    fun move(@AuthenticationPrincipal principal: AccountDetails,
             @PathVariable("gameId") gameId: String,
             @Valid @RequestBody request: TurnRequest): WebGame {
        return service.putMove(principal, gameId, request.playerNumber!!, request.startX!!, request.startY!!,
                request.endX!!, request.endY!!)
    }

    @PostMapping("/{gameId}/turn/push")
    @ApiOperation(value = "Perform Push", nickname = "push")
    @AuthorizationHeaderRequired
    @AuthenticationAndGameFoundRequiredRequestWithContentApiResponses
    fun push(@AuthenticationPrincipal principal: AccountDetails,
             @PathVariable("gameId") gameId: String,
             @Valid @RequestBody request: TurnRequest): WebGame {
        return service.putPush(principal, gameId, request.playerNumber!!, request.startX!!, request.startY!!,
                request.endX!!, request.endY!!)
    }

    @ExceptionHandler(NoSuchGameException::class)
    fun handleNoSuchGameException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND)
    }

    @ExceptionHandler(IllegalEventException::class)
    fun handleIllegalEventException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST)
    }
}