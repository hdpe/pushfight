package me.hdpe.pushfight.engine

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

class Square2dArrayMatcher(val squares: Array<Array<Square>>) : TypeSafeDiagnosingMatcher<Array<Array<Square>>>(Array<Array<Square>>::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("Square array")
    }

    override fun matchesSafely(item: Array<Array<Square>>, mismatchDescription: Description): Boolean {
        var matches = true
        for (y in item.indices) {
            for (x in item[y].indices) {
                val squareMatcher = matchesSquare(squares[y][x])

                if (!squareMatcher.matches(item[y][x])) {
                    matches = false
                    mismatchDescription.appendText("\n($x, $y) ");
                    squareMatcher.describeMismatch(item[y][x], mismatchDescription)
                }
            }
        }
        return matches
    }

    private fun matchesSquare(square: Square): Matcher<Square> {
        return object : TypeSafeDiagnosingMatcher<Square>() {
            override fun describeTo(description: Description) {
                description.appendText("Square")
            }

            override fun matchesSafely(item: Square, mismatchDescription: Description): Boolean {
                if (!square::class.isInstance(item)) {
                    mismatchDescription.appendText("expected: ")
                            .appendValue(square::class)
                            .appendText(", was: ")
                            .appendValue(item::class)
                    return false
                }

                return when(square) {
                    is BoardSquare -> {
                        val actualPiece = (item as BoardSquare).piece
                        if (square.piece == actualPiece) {
                            true
                        } else {
                            mismatchDescription.appendText("expected piece: ")
                                    .appendValue(square.piece)
                                    .appendText(", was: ")
                                    .appendValue(actualPiece)
                            false
                        }
                    }
                    else -> true
                }
            }
        }
    }
}