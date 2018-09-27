package me.hdpe.pushfight.engine.command

import me.hdpe.pushfight.engine.King
import me.hdpe.pushfight.engine.Pawn
import me.hdpe.pushfight.engine.Piece

enum class PieceType(val label: String, val matches: (piece: Piece) -> Boolean) {
    KING("king", {p -> p is King}),
    PAWN("pawn", {p -> p is Pawn})
}