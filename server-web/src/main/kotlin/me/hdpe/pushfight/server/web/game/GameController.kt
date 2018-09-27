package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.IllegalEventException
import me.hdpe.pushfight.server.persistence.NoSuchGameException
import me.hdpe.pushfight.server.persistence.WebGame
import me.hdpe.pushfight.server.web.security.AccountDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/game")
class GameController(val service: GameService) {

    @PostMapping
    fun create(@AuthenticationPrincipal principal: AccountDetails,
               @Valid @RequestBody request: CreateGameRequest): WebGame {
        return service.createGame(principal, request.opponent!!)
    }

    @PostMapping("/{gameId}/setup")
    fun initialPlacements(@AuthenticationPrincipal principal: AccountDetails,
                          @PathVariable("gameId") gameId: String,
                          @Valid @RequestBody request: InitialPlacementsRequest): WebGame {
        return service.putInitialPlacements(principal, gameId, request.playerNumber!!, request.placements!!)
    }

    @PatchMapping("/{gameId}/setup")
    fun updatePlacements(@AuthenticationPrincipal principal: AccountDetails,
                         @PathVariable("gameId") gameId: String,
                         @Valid @RequestBody request: UpdatePlacementsRequest): WebGame {
        return service.putUpdatedPlacements(principal, gameId, request.playerNumber!!, request.placements!!)
    }

    @PostMapping("/{gameId}/setup/confirm")
    fun confirmSetup(@AuthenticationPrincipal principal: AccountDetails,
                     @PathVariable("gameId") gameId: String,
                     @Valid @RequestBody request: ConfirmSetupRequest): WebGame {
        return service.putSetupConfirmation(principal, gameId, request.playerNumber!!)
    }

    @PostMapping("/{gameId}/turn/move")
    fun move(@AuthenticationPrincipal principal: AccountDetails,
             @PathVariable("gameId") gameId: String,
             @Valid @RequestBody request: TurnRequest): WebGame {
        return service.putMove(principal, gameId, request.playerNumber!!, request.startX!!, request.startY!!,
                request.endX!!, request.endY!!)
    }

    @PostMapping("/{gameId}/turn/push")
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