package me.hdpe.pushfight.engine

class Board(val squares: Array<Array<Square>>) {

    fun isSquare(x: Int, y: Int): Boolean {
        return y >= 0 && y < squares.size && x >= 0 && x < squares[0].size && square(x, y) is BoardSquare
    }

    fun getSquare(x: Int, y: Int): BoardSquare {
        return square(x, y) as BoardSquare
    }

    fun isVacant(x: Int, y: Int) = getSquare(x, y).piece === null

    fun withSquare(x: Int, y: Int, square: BoardSquare): Board {
        val newSquares = squares.map { it.copyOf() }.toTypedArray()
        newSquares[y][x] = square
        return Board(newSquares)
    }

    private fun square(x: Int, y: Int): Square {
        return squares[y][x]
    }
}