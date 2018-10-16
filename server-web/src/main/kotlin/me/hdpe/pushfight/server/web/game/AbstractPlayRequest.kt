package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

abstract class AbstractPlayRequest(
        @get:NotNull(message = "{NotNull.playerNumber}")
        @get:Min(value = 1, message = "{Range.playerNumber}")
        @get:Max(value = 2, message = "{Range.playerNumber}")
        var playerNumber: Int?
)
