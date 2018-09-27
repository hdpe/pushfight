package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotBlank

class CreateGameRequest(
        @get:NotBlank
        val opponent: String?
)