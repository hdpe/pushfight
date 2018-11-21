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

    fun withSquareWithPiece(x: Int, y: Int, piece: Piece?): Board {
        val newSquares = squares.map { it.copyOf() }.toTypedArray()
        newSquares[y][x] = (squares[y][x] as BoardSquare).withPiece(piece)
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

    fun coordsOfHattedKing(): Pair<Int, Int>? {
        for (y in 0 until squares.size) {
            for (x in 0 until squares[y].size) {
                val square = squares[y][x]
                if (square is BoardSquare && square.piece is King && square.piece.hatted) {
                    return Pair(x, y)
                }
            }
        }

        return null
    }

    fun getImage(): String {
        val yRange = 0 until squares.size
        val xRange = 0 until squares[0].size

        val buf = StringBuilder()
        for (y in yRange) {
            for (x in xRange) {
                val square = squares[y][x]

                val drawLeftEdge = square is BoardSquare || (x > 0 && squares[y][x - 1] is BoardSquare) ||
                        (y > 0 && squares[y - 1][x] is BoardSquare)
                val drawTopEdge = square is BoardSquare || (y > 0 && squares[y - 1][x] is BoardSquare)

                buf.append(if (drawLeftEdge) "-" else " ")
                        .append(if (drawTopEdge) "-" else " ")

                if (x == xRange.last) {
                    buf.append(if (drawTopEdge) "-" else " ")
                }
            }

            buf.append("\n")

            for (x in xRange) {
                val square = squares[y][x]

                val drawLeftEdge = square is BoardSquare || (x > 0 && squares[y][x - 1] is BoardSquare)

                buf.append(if (drawLeftEdge) "|" else " ")
                        .append(when (square) {
                            is BoardSquare -> {
                                val piece = square.piece
                                when(piece) {
                                    is Piece -> when {
                                        piece.owner.number == 1 && piece is Pawn -> "○"
                                        piece.owner.number == 1 && piece is King -> "□"
                                        piece.owner.number == 2 && piece is Pawn -> "●"
                                        else -> "■"
                                    }
                                    else -> " "
                                }
                            }
                            else -> " "
                        })

                if (x == xRange.last) {
                    buf.append(if (square is BoardSquare) "|" else " ")
                }
            }

            buf.append("\n")

            if (y == yRange.last) {
                for (x in xRange) {
                    val square = squares[y][x]

                    val drawLeftEdge = square is BoardSquare || (x > 0 && squares[y][x - 1] is BoardSquare)
                    val drawBottomEdge = square is BoardSquare

                    buf.append(if (drawLeftEdge) "-" else " ")
                            .append(if (drawBottomEdge) "-" else " ")

                    if (x == xRange.last) {
                        buf.append(if (drawBottomEdge) "-" else " ")
                    }
                }
            }
        }

        return buf.toString()
    }

    private fun square(x: Int, y: Int): Square {
        return squares[y][x]
    }
}