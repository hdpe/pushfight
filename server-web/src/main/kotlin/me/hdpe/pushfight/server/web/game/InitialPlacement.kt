package me.hdpe.pushfight.server.web.game

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import me.hdpe.pushfight.engine.command.PieceType
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class InitialPlacement(
        @get:NotNull(message = "{NotNull.pieceType}")
        @get:Pattern(regexp = "pawn|king", message = "{Pattern.pieceType}")
        @JsonProperty("pieceType")
        var pieceTypeString: String?,

        @get:NotNull(message = "{NotNull.x}")
        var x: Int?,

        @get:NotNull(message = "{NotNull.y}")
        var y: Int?
) {
    @JsonIgnore
    val pieceType: PieceType? = when (pieceTypeString) {
        "pawn" -> PieceType.PAWN
        "king" -> PieceType.KING
        else -> null
    }
}