package me.hdpe.pushfight.engine

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class BoardTest {

    @Test
    fun `getImage with abyss below board returns image`() {
        val board = Board(arrayOf(
                squareArray(BoardSquare()),
                squareArray(AbyssSquare())
        ))

        assertThat(board.getImage(), equalTo(
            "---\n" +
            "| |\n" +
            "---\n" +
            "   \n" +
            "   "
        ))
    }
    @Test
    fun `getImage with complicated board returns image`() {
        val board = Board(arrayOf(
                squareArray(AbyssSquare(), BoardSquare(), AbyssSquare()),
                squareArray(BoardSquare(), BoardSquare(), BoardSquare()),
                squareArray(AbyssSquare(), BoardSquare(), AbyssSquare())
        ))

        assertThat(board.getImage(), equalTo(
            "  ---  \n" +
            "  | |  \n" +
            "-------\n" +
            "| | | |\n" +
            "-------\n" +
            "  | |  \n" +
            "  ---  "
        ))
    }
}