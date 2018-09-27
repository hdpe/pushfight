package me.hdpe.pushfight.engine

import me.hdpe.pushfight.engine.command.PieceType

data class PlayerSetupState(val unplaced: List<Piece>, val complete: Boolean = false) {

    fun getUnplacedPiece(pieceType: PieceType): Piece? {
        return unplaced.find { pieceType.matches(it) }
    }

    fun withPiecePlaced(pieceType: PieceType): PlayerSetupState {
        val pieceIndex = unplaced.indexOfFirst { pieceType.matches(it) }
        return PlayerSetupState(unplaced.filterIndexed { i, _ -> i != pieceIndex }, complete)
    }

    fun withComplete(): PlayerSetupState {
        return PlayerSetupState(unplaced, true)
    }
}