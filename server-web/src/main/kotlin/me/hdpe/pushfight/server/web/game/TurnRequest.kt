package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.server.web.game.AbstractPlayRequest
import javax.validation.constraints.NotNull

class TurnRequest(
        playerNumber: Int?,

        @get:NotNull
        val startX: Int?,

        @get:NotNull
        val startY: Int?,

        @get:NotNull
        val endX: Int?,

        @get:NotNull
        val endY: Int?
) : AbstractPlayRequest(playerNumber)
