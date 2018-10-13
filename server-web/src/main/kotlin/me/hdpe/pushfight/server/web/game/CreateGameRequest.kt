package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotNull

class CreateGameRequest(
        @get:NotNull(message = "{NotNull.opponent}")
        val opponent: String?
)