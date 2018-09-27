package me.hdpe.pushfight.engine

class BoardFactory {
    fun create(): Board {
        return Board(arrayOf(
                squareArray(AbyssSquare(), BoardSquare(), BoardSquare(), AbyssSquare()),
                squareArray(BoardSquare(rail = Rail.LEFT), BoardSquare(), BoardSquare(), AbyssSquare()),
                squareArray(BoardSquare(rail = Rail.LEFT), BoardSquare(), BoardSquare(), BoardSquare(rail = Rail.RIGHT)),
                squareArray(BoardSquare(rail = Rail.LEFT), BoardSquare(), BoardSquare(), BoardSquare(rail = Rail.RIGHT)),
                squareArray(BoardSquare(rail = Rail.LEFT), BoardSquare(), BoardSquare(), BoardSquare(rail = Rail.RIGHT)),
                squareArray(BoardSquare(rail = Rail.LEFT), BoardSquare(), BoardSquare(), BoardSquare(rail = Rail.RIGHT)),
                squareArray(AbyssSquare(), BoardSquare(), BoardSquare(), BoardSquare(rail = Rail.RIGHT)),
                squareArray(AbyssSquare(), BoardSquare(), BoardSquare(), AbyssSquare())
        ));
    }
}