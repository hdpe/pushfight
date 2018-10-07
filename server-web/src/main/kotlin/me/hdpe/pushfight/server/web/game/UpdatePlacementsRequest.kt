package me.hdpe.pushfight.server.web.game

import javax.validation.Valid
import javax.validation.constraints.NotNull

class UpdatePlacementsRequest(
        playerNumber: Int?,

        @get:Valid
        @get:NotNull(message = "{NotNull.UpdatePlacementsRequest.placements}")
        var placements: Array<UpdatedPlacement>?
) : AbstractPlayRequest(playerNumber)
