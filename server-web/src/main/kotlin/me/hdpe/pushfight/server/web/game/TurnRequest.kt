package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.server.web.game.AbstractPlayRequest
import javax.validation.constraints.NotNull

class TurnRequest(
        playerNumber: Int?,

        @get:NotNull(message = "{NotNull.startX")
        val startX: Int?,

        @get:NotNull(message = "{NotNull.startY")
        val startY: Int?,

        @get:NotNull(message = "{NotNull.endX")
        val endX: Int?,

        @get:NotNull(message = "{NotNull.endY")
        val endY: Int?
) : AbstractPlayRequest(playerNumber)
