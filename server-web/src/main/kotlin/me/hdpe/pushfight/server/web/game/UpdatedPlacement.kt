package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotNull

class UpdatedPlacement(
        @get:NotNull(message = "{NotNull.currentX}")
        var currentX: Int?,

        @get:NotNull(message = "{NotNull.currentY}")
        var currentY: Int?,
        
        @get:NotNull(message = "{NotNull.newX}")
        var newX: Int?,
        
        @get:NotNull(message = "{NotNull.newY}")
        var newY: Int?
)