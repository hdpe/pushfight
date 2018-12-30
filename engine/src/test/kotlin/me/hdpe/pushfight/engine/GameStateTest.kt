package me.hdpe.pushfight.engine

import me.hdpe.pushfight.engine.command.InitialPlacementCommand
import me.hdpe.pushfight.engine.command.PieceType
import me.hdpe.pushfight.engine.command.UpdatedPlacementCommand
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hobsoft.hamcrest.compose.ComposeMatchers.compose
import org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

class GameStateTest {

    val player1: Player
    val player2: Player
    val config: GameConfig

    init {
        player1 = newPlayer(1)
        player2 = newPlayer(2)
        config = GameConfig(player1, player2)
    }

    @Nested
    @DisplayName("withInitialPlacements")
    inner class WithInitialPlacementsTest {

        @Test
        fun `withInitialPlacements with game already ended throws exception`() {
            val state = GameState(config, newSetup(), newTurn(), newBoard(), result = newResult())

            val ex = assertThrows<IllegalEventException> { state.withInitialPlacements(player1, listOf()) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended"))
        }

        @Test
        fun `withInitialPlacements with player setup complete throws exception`() {
            val state = GameState(config, completedSetupForPlayer(player1), newTurn(), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withInitialPlacements(player1, listOf()) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.PLAYER_SETUP_COMPLETE, null,
                    "player setup already complete"))
        }

        @Test
        fun `withInitialPlacements with unavailable piece throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> {
                state.withInitialPlacements(player1, listOf(
                        InitialPlacementCommand(PieceType.PAWN, 0, 0), InitialPlacementCommand(PieceType.KING, 1, 0)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.UNAVAILABLE_PIECE, null,
                    "no king still to be initially placed"))
        }

        @Test
        fun `withInitialPlacements with non-board square throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(),
                    singleRowBoard(BoardSquare()))

            val ex = assertThrows<IllegalEventException> {
                state.withInitialPlacements(player1,
                        listOf(InitialPlacementCommand(PieceType.PAWN, 9, 9)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(9, 9),
                    "no such square at (9, 9)"))
        }

        @Test
        fun `withInitialPlacements with non-vacant square throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(),
                    singleRowBoard(BoardSquare(Pawn(player1))))

            val ex = assertThrows<IllegalEventException> {
                state.withInitialPlacements(player1,
                        listOf(InitialPlacementCommand(PieceType.PAWN, 0, 0)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.SQUARE_OCCUPIED, Coordinate(0, 0),
                    "square at (0, 0) is occupied"))
        }

        @Test
        fun `withInitialPlacements with square not in own half throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(),
                    Board(arrayOf(
                            squareArray(BoardSquare(newPiece(player1))),
                            squareArray(BoardSquare())
                    )))

            val ex = assertThrows<IllegalEventException> { state.withInitialPlacements(player1,
                    listOf(InitialPlacementCommand(PieceType.PAWN, 0, 1))) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(0, 1),
                    "square at (0, 1) is in opponent's half"))
        }

        @Test
        fun `withInitialPlacements with square in opponent's half throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(),
                    Board(arrayOf(
                            squareArray(BoardSquare(newPiece(player2)), BoardSquare()),
                            squareArray(BoardSquare(), BoardSquare())
                    )))

            val ex = assertThrows<IllegalEventException> { state.withInitialPlacements(player1,
                    listOf(InitialPlacementCommand(PieceType.PAWN, 1, 0))) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(1, 0),
                    "square at (1, 0) is in opponent's half"))
        }

        @Test
        fun `withInitialPlacements with legal placements returns new GameState`() {
            val pieces = listOf(Pawn(player1), Pawn(player1), King(player1))

            val before = GameState(config, newPlayer1Setup(pieces), newTurn(),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val after = before.withInitialPlacements(player1, listOf(
                    InitialPlacementCommand(PieceType.KING, 0, 0),
                    InitialPlacementCommand(PieceType.PAWN, 1, 0)))

            assertThat(after, matchesGameState(config, matchesSetupWithPlayer1Unplaced(pieces[1]),
                    board = matchesBoard(arrayOf(squareArray(BoardSquare(pieces[2]), BoardSquare(pieces[0]))))))
        }
    }

    @Nested
    @DisplayName("withUpdatedPlacements")
    inner class WithUpdatedPlacementsTest {

        @Test
        fun `withUpdatedPlacements with game already ended throws exception`() {
            val state = GameState(config, newSetup(), newTurn(), newBoard(), result = newResult())

            val ex = assertThrows<IllegalEventException> { state.withUpdatedPlacements(player1, listOf()) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended"))
        }

        @Test
        fun `withUpdatedPlacements with player setup complete throws exception`() {
            val state = GameState(config, completedSetupForPlayer(player1), newTurn(), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withUpdatedPlacements(player1, listOf()) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.PLAYER_SETUP_COMPLETE, null,
                    "player setup already complete"))
        }

        @Test
        fun `withUpdatedPlacements with opponent's piece throws exception`() {
            val state = GameState(config, incompleteSetupForPlayer(player1), newTurn(),
                    singleRowBoard(BoardSquare(Pawn(player2)), BoardSquare()))

            val ex = assertThrows<IllegalEventException> {
                state.withUpdatedPlacements(player1,
                        listOf(UpdatedPlacementCommand(0, 0, 1, 0)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.WRONG_PLAYER_PIECE, Coordinate(0, 0),
                    "square at (0, 0) has wrong player's piece"))
        }

        @Test
        fun `withUpdatedPlacements with non-board target square throws exception`() {
            val state = GameState(config, incompleteSetupForPlayer(player1), newTurn(),
                    singleRowBoard(BoardSquare(Pawn(player1))))

            val ex = assertThrows<IllegalEventException> {
                state.withUpdatedPlacements(player1,
                        listOf(UpdatedPlacementCommand(0, 0, 9, 9)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(9, 9),
                    "no such square at (9, 9)"))
        }

        @Test
        fun `withUpdatedPlacements with non-vacant target square throws exception`() {
            val state = GameState(config, incompleteSetupForPlayer(player1), newTurn(),
                    singleRowBoard(BoardSquare(Pawn(player1)), BoardSquare(Pawn(player1))))

            val ex = assertThrows<IllegalEventException> {
                state.withUpdatedPlacements(player1,
                        listOf(UpdatedPlacementCommand(0, 0, 1, 0)))
            }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.SQUARE_OCCUPIED, Coordinate(1, 0),
                    "square at (1, 0) is occupied"))
        }

        @Test
        fun `withUpdatedPlacements with square not in own half throws exception`() {
            val state = GameState(config, incompleteSetupForPlayer(player1), newTurn(),
                    Board(arrayOf(
                            squareArray(BoardSquare(newPiece(player1)), BoardSquare(newPiece(player1))),
                            squareArray(BoardSquare(newPiece()), BoardSquare())
                    )))

            val ex = assertThrows<IllegalEventException> { state.withUpdatedPlacements(player1,
                    listOf(UpdatedPlacementCommand(1, 0, 1, 1))) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(1, 1),
                    "square at (1, 1) is in opponent's half"))
        }

        @Test
        fun `withUpdatedPlacements with square in opponent's half throws exception`() {
            val state = GameState(config, incompleteSetupForPlayer(player1), newTurn(),
                    Board(arrayOf(
                            squareArray(BoardSquare(newPiece(player2)), BoardSquare()),
                            squareArray(BoardSquare(newPiece(player1)), BoardSquare())
                    )))

            val ex = assertThrows<IllegalEventException> { state.withUpdatedPlacements(player1,
                    listOf(UpdatedPlacementCommand(0, 1, 1, 0))) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(1, 0),
                    "square at (1, 0) is in opponent's half"))
        }

        @Test
        fun `withUpdatedPlacements with legal placements returns new GameState`() {
            val (piece1, piece2) = listOf(Pawn(player1), King(player1))
            val turn = newTurn()

            val before = GameState(config, incompleteSetupForPlayer(player1), turn,
                    singleRowBoard(BoardSquare(piece1), BoardSquare(piece2), BoardSquare()))

            val after = before.withUpdatedPlacements(player1, listOf(UpdatedPlacementCommand(0, 0, 2, 0)))

            assertThat(after, matchesGameState(config, board = matchesBoard(arrayOf(
                    squareArray(BoardSquare(), BoardSquare(piece2), BoardSquare(piece1)))), turn = equalTo(turn)))
        }
    }

    @Nested
    @DisplayName("withSetupConfirmed")
    inner class WithSetupConfirmedTest {

        @Test
        fun `withSetupConfirmed with game already ended throws exception`() {
            val state = GameState(config, newSetup(), newTurn(), newBoard(), result = newResult())

            val ex = assertThrows<IllegalEventException> { state.withSetupConfirmed(player1) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended"))
        }

        @Test
        fun `withSetupConfirmed with player setup complete throws exception`() {
            val state = GameState(config, completedSetupForPlayer(player1), newTurn(), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withSetupConfirmed(player1) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.PLAYER_SETUP_COMPLETE, null,
                    "player setup already complete"))
        }

        @Test
        fun `withSetupConfirmed with pieces remaining unplaced throws exception`() {
            val state = GameState(config, newPlayer1Setup(listOf(Pawn(player1))), newTurn(), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withSetupConfirmed(player1) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.PIECES_NOT_PLACED, null,
                    "not all pieces have been placed"))
        }

        @Test
        fun `withSetupConfirmed with legal confirmation returns new GameState`() {
            val before = GameState(config, incompleteSetupForPlayer(player1), newTurn(), singleRowBoard(BoardSquare()))

            val after = before.withSetupConfirmed(player1)

            assertThat(after, matchesGameState(config, matchesSetupWithPlayer1SetupComplete(),
                    board = matchesBoard(arrayOf(squareArray(BoardSquare())))))
        }
    }

    @Nested
    @DisplayName("withMove")
    inner class WithMoveTest {

        @Test
        fun `withMove with game already ended throws exception`() {
            val state = GameState(config, newSetup(), newTurn(), newBoard(), result = newResult())

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended"))
        }

        @Test
        fun `withMove with setup not complete throws exception`() {
            val state = GameState(config, incompleteSetup(), newTurn(player1), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.INCOMPLETE_SETUP, null,
                    "setup phase not complete yet"))
        }

        @Test
        fun `withMove on other player's turn throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withMove(player2, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OUT_OF_TURN, null, "not your turn"))
        }

        @Test
        fun `withMove with no moves remaining throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1, moves = 2), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_MOVES_REMAINING, null,
                    "no moves remaining"))
        }

        @Test
        fun `withMove with abyss square at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(AbyssSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(0, 0),
                    "no such square at (0, 0)"))
        }

        @ParameterizedTest
        @CsvSource("-1, -1", "0, -1", "1, -1", "2, -1", "2, 0", "2, 1", "1, 1", "0, 1", "-1, 1", "-1, 0")
        fun `withMove with off board rectangle coord at start throws exception`(x: Int, y: Int) {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, x, y, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(x, y),
                    "no such square at ($x, $y)"))
        }

        @Test
        fun `withMove with no piece at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.SQUARE_EMPTY, Coordinate(0, 0),
                    "no piece at (0, 0)"))
        }

        @Test
        fun `withMove with opponent piece at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newPiece(player2)), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.WRONG_PLAYER_PIECE, Coordinate(0, 0),
                    "square at (0, 0) has wrong player's piece"))
        }

        @Test
        fun `withMove with abyss square at end throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newPiece(player1)), AbyssSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(1, 0),
                    "no such square at (1, 0)"))
        }

        @ParameterizedTest
        @CsvSource("-1, -1", "0, -1", "1, -1", "2, -1", "2, 0", "2, 1", "1, 1", "0, 1", "-1, 1", "-1, 0")
        fun `withMove with off-board-rectangle coord at end throws exception`(x: Int, y: Int) {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newPiece(player1)), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, x, y) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(x, y),
                    "no such square at ($x, $y)"))
        }

        @ParameterizedTest
        @MethodSource("players")
        fun `withMove with piece at end throws exception`(player: Player) {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newPiece(player1)), BoardSquare(newPiece(player))))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.SQUARE_OCCUPIED, Coordinate(1, 0),
                    "square at (1, 0) is occupied"))
        }

        @Test
        fun `withMove with no path to end throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1), Board(arrayOf(
                    squareArray(AbyssSquare(), BoardSquare()),
                    squareArray(BoardSquare(newPiece(player1)), BoardSquare(newPiece(player2))))))

            val ex = assertThrows<IllegalEventException> { state.withMove(player1, 0, 1, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_PATH_TO_SQUARE, Coordinate(1, 0),
                    "no path to square (1, 0)"))
        }

        @Test
        fun `withMove with legal move returns new GameState`() {
            val pieceToMove = newPiece(player1)
            val before = GameState(config, completedSetup(), newTurn(player1, moves = 0), Board(arrayOf(
                    squareArray(AbyssSquare(), BoardSquare()),
                    squareArray(BoardSquare(pieceToMove), BoardSquare()))))

            val after = before.withMove(player1, 0, 1, 1, 0)

            assertThat(after, matchesGameState(config,
                    turn = matchesTurn(moves = equalTo(1)),
                    board = matchesBoard(arrayOf(
                        squareArray(AbyssSquare(), BoardSquare(pieceToMove)),
                        squareArray(BoardSquare(), BoardSquare())
                    )),
                    result = matchesNullResultState()
            ))
        }

        @ParameterizedTest
        @CsvSource("0, 1, 0", "90, 0, 1", "180, -1, 0", "270, 0, -1")
        fun `withMove considers moves legal regardless of move direction`(rotation: Int, deltaX: Int, deltaY: Int) {
            val pieceToMove = newPiece(player1)

            val (rotated, origin) = rotate(arrayOf(
                    squareArray(BoardSquare(pieceToMove), BoardSquare()),
                    squareArray(AbyssSquare(), AbyssSquare())
            ), rotation)

            val (originX, originY) = origin
            val newX = originX + deltaX
            val newY = originY + deltaY

            val before = GameState(config, completedSetup(), newTurn(player1), Board(rotated))

            val after = before.withMove(player1, originX, originY, newX, newY)

            assertThat(after.board.getSquare(newX, newY).piece, isA(Piece::class.java))
        }

        @Test
        fun `withMove with terminal move with possible subsequent push returns new GameState with no result`() {
            val before = GameState(config, completedSetup(), newTurn(player1, moves = 1),
                    Board(arrayOf(squareArray(BoardSquare(newKing(player1)), BoardSquare(),
                            BoardSquare(newPawn(player1))))))

            val after = before.withMove(player1, 0, 0, 1, 0)

            assertThat(after.result, nullValue())
        }

        @ParameterizedTest
        @MethodSource("terminalMoveFinalAdjacentSquares")
        fun `withMove with terminal move with no possible subsequent push returns new GameState with opponent as victor`(finalSquare: Square) {
            val before = GameState(config, completedSetup(), newTurn(player1, moves = 1),
                    Board(arrayOf(squareArray(BoardSquare(newKing(player1)), BoardSquare(), finalSquare))))

            val after = before.withMove(player1, 0, 0, 1, 0)

            assertThat(after.result, matchesResultState(victor = equalTo(player2)))
        }

        private fun terminalMoveFinalAdjacentSquares(): Array<Square> =
                arrayOf(AbyssSquare(), BoardSquare(newPawn(), Rail.RIGHT), BoardSquare(newKing(player1, hatted = true)))

        private fun rotate(squares: Array<Array<Square>>, rotation: Int): Pair<Array<Array<Square>>, Pair<Int, Int>> {
            val newSquares = (0..rotation / 90).fold(squares) { s, _ -> if (s === squares) s.copyOf() else rotate90(s) }
            val newOrigin = getSquareCoordinates(squares[0][0], newSquares)

            return Pair(newSquares, newOrigin)
        }

        private fun rotate90(squares: Array<Array<Square>>): Array<Array<Square>> =
                Array(squares.size) { row -> Array(squares.size) { col -> squares[squares.size - col - 1][row] } }

        private fun getSquareCoordinates(needle: Square, haystack: Array<Array<Square>>): Pair<Int, Int> {
            return haystack
                    .mapIndexed { y, row -> Pair(y, row) }
                    .flatMap { (y, row) ->
                        row.mapIndexed { x, sq -> Pair(x, sq) }
                                .filter { (_, sq) -> sq === needle }
                                .map { (x, _) -> Pair(x, y) }
                    }
                    .first()
        }

        private fun players() = arrayOf(player1, player2)
    }

    @Nested
    @DisplayName("withPush")
    inner class WithPushTest {

        @Test
        fun `withPush with game already ended throws exception`() {
            val state = GameState(config, newSetup(), newTurn(), newBoard(), result = newResult())

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended"))
        }

        @Test
        fun `withPush with setup not complete throws exception`() {
            val state = GameState(config, incompleteSetup(), newTurn(player1), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.INCOMPLETE_SETUP, null,
                    "setup phase not complete yet"))
        }

        @Test
        fun `withPush on other player's turn throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1), singleRowBoard())

            val ex = assertThrows<IllegalEventException> { state.withPush(player2, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.OUT_OF_TURN, null, "not your turn"))
        }

        @Test
        fun `withPush with abyss square throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(AbyssSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(0, 0),
                    "no such square at (0, 0)"))
        }

        @ParameterizedTest
        @CsvSource("-1, -1", "0, -1", "1, -1", "2, -1", "2, 0", "2, 1", "1, 1", "0, 1", "-1, 1", "-1, 0")
        fun `withPush with off board rectangle coord at start throws exception`(x: Int, y: Int) {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, x, y, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(x, y),
                    "no such square at ($x, $y)"))
        }

        @Test
        fun `with no piece at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.SQUARE_EMPTY, Coordinate(0, 0),
                    "no piece at (0, 0)"))
        }

        @Test
        fun `withPush with opponent piece at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player2)), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.WRONG_PLAYER_PIECE, Coordinate(0, 0),
                    "square at (0, 0) has wrong player's piece"))
        }

        @Test
        fun `withPush with pawn piece at start throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newPawn(player1)), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.PAWN_CANNOT_PUSH, Coordinate(0, 0),
                    "square at (0, 0) has pawn, and pawns can't push"))
        }

        @Test
        fun `withPush with abyss square at end throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), AbyssSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(1, 0),
                    "no such square at (1, 0)"))
        }

        @Test
        fun `withPush with non-adjacent square at end throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), BoardSquare(), BoardSquare()))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 2, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.NON_ADJACENT_SQUARE, Coordinate(2, 0),
                    "can't push: square at (2, 0) is not adjacent"))
        }

        @Test
        fun `withPush with hatted king in push throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), BoardSquare(newPiece()),
                            BoardSquare(newKing(player2, hatted = true))))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.HATTED_KING, Coordinate(2, 0),
                    "can't push: king at (2, 0) has a hat"))
        }

        @Test
        fun `withPush with rail behind push throws exception`() {
            val state = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), BoardSquare(newPiece()),
                            BoardSquare(newPiece(), Rail.RIGHT)))

            val ex = assertThrows<IllegalEventException> { state.withPush(player1, 0, 0, 1, 0) }

            assertThat(ex, matchesIllegalEventException(IllegalEventReason.RAIL_PREVENTING_PUSH, Coordinate(2, 0),
                    "can't push: square at (2, 0) has a rail"))
        }

        @Test
        fun `withPush with legal push returns new GameState with board pieces as expected`() {
            val pieceToBePushed = newPiece()
            val before = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player2, hatted = true)), BoardSquare(newKing(player1)),
                            BoardSquare(pieceToBePushed), BoardSquare()))

            val after = before.withPush(player1, 1, 0, 2, 0)

            val squarePieces = after.board.squares[0].filterIsInstance<BoardSquare>().map { it.piece }

            assertThat(squarePieces, contains(matchesKing(owner = player2, hatted = false), nullValue(),
                    matchesKing(owner = player1, hatted = true), equalTo(pieceToBePushed)))
        }

        @Test
        fun `withPush with legal, non-terminal push returns new GameState with no victor`() {
            val before = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), BoardSquare(newPiece()), BoardSquare()))

            val after = before.withPush(player1, 0, 0, 1, 0)

            assertThat(after, matchesGameState(config, result = matchesNullResultState()))
        }

        @Test
        fun `withPush with legal, terminal push returns new GameState with victor`() {
            val before = GameState(config, completedSetup(), newTurn(player1),
                    singleRowBoard(BoardSquare(newKing(player1)), BoardSquare(newPiece(player2))))

            val after = before.withPush(player1, 0, 0, 1, 0)

            assertThat(after, matchesGameState(config, result = matchesResultState(equalTo(player1))))
        }

        @Test
        fun `withPush with legal push for player 1 returns new GameState with next player's turn`() {
            val before = GameState(config, completedSetup(), newTurn(player1, number = 1, moves = 1),
                    singleRowBoard(BoardSquare(King(player1)), BoardSquare(newPiece()), BoardSquare()))

            val after = before.withPush(player1, 0, 0, 1, 0)

            assertThat(after.turn, matchesTurn(number = equalTo(1), moves = equalTo(0), player = equalTo(player2)))
        }

        @Test
        fun `withPush with legal push for player 2 returns new GameState with next player's turn`() {
            val before = GameState(config, completedSetup(), newTurn(player2, number = 1, moves = 1),
                    singleRowBoard(BoardSquare(King(player2)), BoardSquare(newPiece()), BoardSquare()))

            val after = before.withPush(player2, 0, 0, 1, 0)

            assertThat(after.turn, matchesTurn(number = equalTo(2), moves = equalTo(0), player = equalTo(player1)))
        }
    }

    private fun newSetup(): SetupState = completedSetup()

    private fun completedSetup(): SetupState {
        return SetupState(PlayerSetupState(listOf(), true), PlayerSetupState(listOf(), true))
    }

    private fun completedSetupForPlayer(player: Player): SetupState {
        return SetupState(PlayerSetupState(listOf(), player == player1), PlayerSetupState(listOf(), player == player2))
    }

    private fun incompleteSetup(): SetupState {
        return SetupState(PlayerSetupState(listOf(), true), PlayerSetupState(listOf(), false))
    }

    private fun incompleteSetupForPlayer(player: Player): SetupState {
        return SetupState(PlayerSetupState(listOf(), player != player1), PlayerSetupState(listOf(), player != player2))
    }

    private fun newPlayer1Setup(player1UnplacedPieces: List<Piece>): SetupState {
        return SetupState(PlayerSetupState(player1UnplacedPieces), PlayerSetupState(listOf()))
    }

    private fun newTurn(player: Player = player1, number: Int = 1, moves: Int = 0): TurnState {
        return TurnState(number, player, moves)
    }

    private fun newBoard() = singleRowBoard()

    private fun singleRowBoard(vararg squares: Square): Board = Board(arrayOf(squareArray(*squares)))

    private fun newPlayer(number: Int = 1): Player = object : Player { override val number: Int get() = number }

    private fun newPiece(player: Player = player1): Piece = newPawn(player)

    private fun newPawn(player: Player = player1): Piece = Pawn(player)

    private fun newKing(player: Player = player1, hatted: Boolean = false): Piece = King(player, hatted)

    private fun newResult(victor: Player = player1): ResultState {
        return ResultState(victor)
    }

    private fun matchesIllegalEventException(reason: IllegalEventReason, coord: Coordinate?, message: String):
            Matcher<IllegalEventException> {
        return compose("an IllegalEventException with",
                hasFeature("reason", IllegalEventException::reason, equalTo(reason)),
                hasFeature("coordinate", IllegalEventException::coordinate, equalTo(coord)),
                hasFeature("message", IllegalEventException::message, equalTo(message)))
    }

    private fun matchesGameState(config: GameConfig, setup: Matcher<SetupState> = any(SetupState::class.java),
                                 turn: Matcher<TurnState> = any(TurnState::class.java),
                                 board: Matcher<Board> = any(Board::class.java),
                                 result: Matcher<ResultState?> = anyOf(any(ResultState::class.java), nullValue())): Matcher<GameState> {
        return compose("a GameState with",
                hasFeature("config", GameState::config, sameInstance(config)),
                hasFeature("setup", GameState::setup, setup),
                hasFeature("turn", GameState::turn, turn),
                hasFeature("board", GameState::board, board),
                hasFeature("result", { it.result }, result));
    }

    private fun matchesSetupWithPlayer1Unplaced(vararg unplaced: Piece): Matcher<SetupState> {
        return compose("a SetupState with",
                hasFeature("player1Setup", SetupState::player1Setup, compose("a PlayerSetupState with",
                        hasFeature("unplaced", PlayerSetupState::unplaced, contains(*unplaced)))))
    }

    private fun matchesSetupWithPlayer1SetupComplete(): Matcher<SetupState> {
        return compose("a SetupState with",
                hasFeature("player1Setup", SetupState::player1Setup, compose("a PlayerSetupState with",
                        hasFeature("complete", PlayerSetupState::complete, equalTo(true)))))
    }

    private fun matchesTurn(number: Matcher<Int> = any(Int::class.java),
                            player: Matcher<Player> = any(Player::class.java),
                            moves: Matcher<Int> = any(Int::class.java)): Matcher<TurnState> {
        return compose("a TurnState with",
                hasFeature("number", TurnState::number, number),
                hasFeature("player", TurnState::player, player),
                hasFeature("moves", TurnState::moves, moves))
    }

    private fun matchesBoard(squares: Array<Array<Square>>): Matcher<Board> {
        return compose("a Board with", hasFeature("squares", Board::squares, Square2dArrayMatcher(squares)))
    }

    private fun matchesKing(owner: Player, hatted: Boolean): Matcher<Piece?> {
        return allOf(isA(Piece::class.java), hasProperty("owner", equalTo(owner)), hasProperty("hatted", equalTo(hatted)))
    }

    private fun matchesResultState(victor: Matcher<Player>): Matcher<ResultState?> {
        return compose("a ResultState with", hasFeature("victor", { it!!.victor }, victor));
    }

    private fun matchesNullResultState() = nullValue(ResultState::class.java)
}