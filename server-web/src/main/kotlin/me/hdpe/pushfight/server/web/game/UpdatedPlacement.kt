package me.hdpe.pushfight.server.web.game

import javax.validation.constraints.NotNull

class UpdatedPlacement(
        @get:NotNull(message = "{NotNull.UpdatedPlacement.currentX}")
        val currentX: Int?,

        @get:NotNull(message = "{NotNull.UpdatedPlacement.currentY}")
        val currentY: Int?,
        
        @get:NotNull(message = "{NotNull.UpdatedPlacement.newX}")
        val newX: Int?,
        
        @get:NotNull(message = "{NotNull.UpdatedPlacement.newY}")
        val newY: Int?
)