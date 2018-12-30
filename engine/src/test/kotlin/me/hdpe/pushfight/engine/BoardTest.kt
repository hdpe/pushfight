package me.hdpe.pushfight.engine

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hobsoft.hamcrest.compose.ComposeMatchers.compose
import org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BoardTest {

    @Nested
    @DisplayName("withSquareWithPiece")
    inner class WithSquareWithPieceTest {
        @Test
        fun `withSquareWithPiece returns square with piece`() {
            val board = Board(arrayOf(squareArray(BoardSquare(null, Rail.LEFT))))

            val pawn = Pawn(newPlayer())

            assertThat(board.withSquareWithPiece(0, 0, pawn), matchesBoard(
                    arrayOf(squareArray(BoardSquare(pawn, Rail.LEFT)))
            ))
        }
    }

    @Nested
    @DisplayName("getImage")
    inner class GetImageTest {
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

    private fun newPlayer(number: Int = 1): Player = object : Player { override val number: Int get() = number }

    private fun matchesBoard(squares: Array<Array<Square>>): Matcher<Board> {
        return compose("a Board with", hasFeature("squares", { it.squares }, Square2dArrayMatcher(squares)))
    }

    private fun matchesKing(owner: Player, hatted: Boolean): Matcher<Piece?> {
        return allOf(
                Matchers.isA(Piece::class.java),
                Matchers.hasProperty("owner", equalTo(owner)),
                Matchers.hasProperty("hatted", equalTo(hatted))
        )
    }
}