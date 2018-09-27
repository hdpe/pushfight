package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

abstract class AbstractPlayRequest(
        @get:NotBlank(message = "{NotBlank.AbstractPlayRequest.playerNumber}")
        @get:Size(min = 1, max = 2, message = "{Size.AbstractPlayRequest.playerNumber}")
        val playerNumber: Int?
)
