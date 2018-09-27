package me.hdpe.pushfight.server.web.game

import javax.validation.Valid
import javax.validation.constraints.NotNull

class InitialPlacementsRequest(
        playerNumber: Int?,

        @get:Valid
        @get:NotNull(message = "{NotNull.placements}")
        val placements: List<InitialPlacement>?
) : AbstractPlayRequest(playerNumber)
