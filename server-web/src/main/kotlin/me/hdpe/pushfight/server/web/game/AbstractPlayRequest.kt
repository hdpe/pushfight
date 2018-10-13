package me.hdpe.pushfight.server.web.game

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

abstract class AbstractPlayRequest(
        @get:NotNull(message = "{NotNull.playerNumber}")
        @get:Range(min = 1, max = 2, message = "{Range.playerNumber}")
        var playerNumber: Int?
)
