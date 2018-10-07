package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotNull

class UpdatedPlacement(
        @get:NotNull(message = "{NotNull.UpdatedPlacement.currentX}")
        var currentX: Int?,

        @get:NotNull(message = "{NotNull.UpdatedPlacement.currentY}")
        var currentY: Int?,
        
        @get:NotNull(message = "{NotNull.UpdatedPlacement.newX}")
        var newX: Int?,
        
        @get:NotNull(message = "{NotNull.UpdatedPlacement.newY}")
        var newY: Int?
)