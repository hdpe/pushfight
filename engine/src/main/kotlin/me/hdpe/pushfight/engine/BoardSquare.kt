package me.hdpe.pushfight.engine

class BoardSquare(val piece: Piece? = null, val rail: Rail = Rail.NONE) : Square {

    fun withPiece(piece: Piece?): BoardSquare {
        return BoardSquare(piece, rail)
    }
}
