package me.hdpe.pushfight.engine

class Board(val squares: Array<Array<Square>>) {

    fun isSquare(x: Int, y: Int): Boolean {
        return y >= 0 && y < squares.size && x >= 0 && x < squares[0].size && square(x, y) is BoardSquare
    }

    fun getSquare(x: Int, y: Int): BoardSquare {
        return square(x, y) as BoardSquare
    }

    fun getSquaresInHalf(y: Int): Array<BoardSquare> {
        val yRange = if (y < squares.size / 2) 0 until squares.size / 2 else squares.size / 2 until squares.size - 1

        return squares.sliceArray(yRange).fold(arrayOf()) { acc, row -> acc + row.filterIsInstance<BoardSquare>() }
    }

    fun isVacant(x: Int, y: Int) = getSquare(x, y).piece === null

    fun withSquare(x: Int, y: Int, square: BoardSquare): Board {
        val newSquares = squares.map { it.copyOf() }.toTypedArray()
        newSquares[y][x] = square
        return Board(newSquares)
    }

    fun forEachOccupiedSquareAffectedByPotentialPush(startX: Int, startY: Int, xDelta: Int, yDelta: Int,
                                                             action: (square: BoardSquare, x: Int, y: Int) -> Unit) {
        var x = startX
        var y = startY

        while (isSquare(x, y) && !isVacant(x, y)) {
            action(getSquare(x, y), x, y)
            x += xDelta
            y += yDelta
        }
    }

    private fun square(x: Int, y: Int): Square {
        return squares[y][x]
    }
}