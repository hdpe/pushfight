package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.command.PieceType
import javax.validation.constraints.NotNull

class InitialPlacement(
        @get:NotNull(message = "{NotNull.InitialPlacement.pieceType}")
        val pieceType: PieceType?,

        @get:NotNull(message = "{NotNull.InitialPlacement.x}")
        val x: Int?,

        @get:NotNull(message = "{NotNull.InitialPlacement.y}")
        val y: Int?
)