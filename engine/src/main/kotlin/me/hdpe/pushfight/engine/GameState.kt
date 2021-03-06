package me.hdpe.pushfight.engine

import me.hdpe.pushfight.engine.command.InitialPlacementCommand
import me.hdpe.pushfight.engine.command.PieceType
import me.hdpe.pushfight.engine.command.UpdatedPlacementCommand

class GameState(val config: GameConfig, val setup: SetupState, val turn: TurnState, val board: Board,
                val result: ResultState? = null) {

    fun withInitialPlacements(player: Player, placements: List<InitialPlacementCommand>): GameState {
        verifyGameInProgress()
        verifySetupIncomplete(player)
        
        return placements.fold(this) { state, placement -> state.withInitialPlacement(player, placement) }
    }
    
    private fun withInitialPlacement(player: Player, placement: InitialPlacementCommand): GameState {
        verifyPieceUnplaced(player, placement.pieceType)

        verifyBoardSquare(placement.x, placement.y)
        verifyVacantSquare(placement.x, placement.y)
        verifyOwnHalf(player, placement.x, placement.y)
        
        return applyInitialPlacement(player, placement)
    }

    fun withUpdatedPlacements(player: Player, placements: List<UpdatedPlacementCommand>): GameState {
        verifyGameInProgress()
        verifySetupIncomplete(player)

        return placements.fold(this) { state, placement -> state.withUpdatedPlacement(player, placement) }
    }

    private fun withUpdatedPlacement(player: Player, placement: UpdatedPlacementCommand): GameState {
        verifyPlayerPiece(player, placement.currentX, placement.currentY)

        verifyBoardSquare(placement.newX, placement.newY)
        verifyVacantSquare(placement.newX, placement.newY)
        verifyOwnHalf(player, placement.newX, placement.newY)

        return applyMove(placement.currentX, placement.currentY, placement.newX, placement.newY)
    }

    fun withSetupConfirmed(player: Player): GameState {
        verifyGameInProgress()
        verifySetupIncomplete(player)
        verifyNoPiecesUnplaced(player)
        
        return applySetupConfirmed(player)
    }

    fun withMove(player: Player, startX: Int, startY: Int, endX: Int, endY: Int): GameState {
        verifyGameInProgress()
        verifySetupComplete()
        verifyPlayerTurn(player)
        verifyMovesRemaining()

        verifyBoardSquare(startX, startY)
        verifyPlayerPiece(player, startX, startY)

        verifyBoardSquare(endX, endY)
        verifyVacantSquare(endX, endY)

        verifyVacantPath(startX, startY, endX, endY)

        return applyMove(startX, startY, endX, endY, asMoveTurn = true)
    }

    fun withPush(player: Player, startX: Int, startY: Int, endX: Int, endY: Int): GameState {
        verifyGameInProgress()
        verifySetupComplete()
        verifyPlayerTurn(player)

        verifyBoardSquare(startX, startY)
        verifyPlayerPiece(player, startX, startY)
        verifyKingPiece(startX, startY)

        verifyBoardSquare(endX, endY)
        verifyAdjacent(startX, startY, endX, endY)

        val xDelta = endX - startX
        val yDelta = endY - startY

        verifyNoPieceWithHatPreventingPush(startX, startY, xDelta, yDelta)
        verifyNoRailPreventingPush(startX, startY, xDelta, yDelta)

        return applyPush(startX, startY, xDelta, yDelta)
    }

    fun withResign(player: Player): GameState {
        verifyGameInProgress()

        return applyResign(player)
    }

    private fun verifyGameInProgress() {
        if (result != null) {
            throw IllegalEventException(IllegalEventReason.GAME_ENDED, null, "game has already ended")
        }
    }

    private fun verifySetupIncomplete(player: Player) {
        if (setup.isComplete(getPlayerNumber(player))) {
            throw IllegalEventException(IllegalEventReason.PLAYER_SETUP_COMPLETE, null, "player setup already complete")
        }
    }

    private fun verifySetupComplete() {
        if (!setup.isComplete()) {
            throw IllegalEventException(IllegalEventReason.INCOMPLETE_SETUP, null, "setup phase not complete yet")
        }
    }

    private fun verifyPieceUnplaced(player: Player, pieceType: PieceType) {
        if (setup.getUnplacedPiece(getPlayerNumber(player), pieceType) == null) {
            throw IllegalEventException(IllegalEventReason.UNAVAILABLE_PIECE, null,
                    "no ${pieceType.label} still to be initially placed")
        }
    }

    private fun verifyBoardSquare(x: Int, y: Int) {
        if (!board.isSquare(x, y)) {
            throw IllegalEventException(IllegalEventReason.NO_SUCH_SQUARE, Coordinate(x, y),
                    "no such square at ($x, $y)")
        }
    }

    private fun verifyVacantSquare(x: Int, y: Int) {
        if (!board.isVacant(x, y)) {
            throw IllegalEventException(IllegalEventReason.SQUARE_OCCUPIED, Coordinate(x, y),
                    "square at ($x, $y) is occupied")
        }
    }

    private fun verifyOwnHalf(player: Player, x: Int, y: Int) {
        if (isPlayerPieceInHalf(getOpponent(player), y) || isPlayerPieceInHalf(player, y, oppositeHalf = true)) {
            throw IllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(x, y),
                    "square at ($x, $y) is in opponent's half")
        }
    }

    private fun isPlayerPieceInHalf(player: Player, y: Int, oppositeHalf: Boolean = false): Boolean {
        return board.getSquaresInHalf(y, oppositeHalf).any { it.piece?.owner == player }
    }

    private fun verifyNoPiecesUnplaced(player: Player) {
        if (setup.isUnplacedPiecesRemaining(getPlayerNumber(player))) {
            throw IllegalEventException(IllegalEventReason.PIECES_NOT_PLACED, null, "not all pieces have been placed")
        }
    }

    private fun verifyPlayerPiece(player: Player?, x: Int, y: Int) {
        with(board.getSquare(x, y)) {
            if (piece === null) {
                throw IllegalEventException(IllegalEventReason.SQUARE_EMPTY, Coordinate(x, y), "no piece at ($x, $y)")
            }
            if (piece.owner != player) {
                throw IllegalEventException(IllegalEventReason.WRONG_PLAYER_PIECE, Coordinate(x, y),
                        "square at ($x, $y) has wrong player's piece")
            }
        }
    }

    private fun verifyPlayerTurn(player: Player) {
        if (turn.player != player) {
            throw IllegalEventException(IllegalEventReason.OUT_OF_TURN, null, "not your turn")
        }
    }

    private fun verifyMovesRemaining() {
        if (turn.moves == 2) {
            throw IllegalEventException(IllegalEventReason.NO_MOVES_REMAINING, null, "no moves remaining")
        }
    }

    private fun verifyVacantPath(startX: Int, startY: Int, endX: Int, endY: Int) {
        if (!testVacantPath(startX, startY, endX, endY)) {
            throw IllegalEventException(IllegalEventReason.NO_PATH_TO_SQUARE, Coordinate(endX, endY),
                    "no path to square ($endX, $endY)")
        }
    }

    private fun testVacantPath(currentX: Int, currentY: Int, endX: Int, endY: Int,
                               path: LinkedHashSet<Pair<Int, Int>> = LinkedHashSet()): Boolean {
        if (Pair(currentX, currentY) == Pair(endX, endY)) {
            return true
        }

        val updatedPath = LinkedHashSet(path + Pair(currentX, currentY))

        for ((nx, ny) in getVacantNeighbours(currentX, currentY).filter { !path.contains(it) }) {
            if (testVacantPath(nx, ny, endX, endY, updatedPath)) {
                return true
            }
        }

        return false
    }

    private fun getVacantNeighbours(x: Int, y: Int): List<Pair<Int, Int>> {
        return board.getNeighbourCoords(x, y) { square -> square.piece == null }
    }

    private fun verifyKingPiece(x: Int, y: Int) {
        if (!isKingPiece(x, y)) {
            throw IllegalEventException(IllegalEventReason.PAWN_CANNOT_PUSH, Coordinate(x, y),
                    "square at ($x, $y) has pawn, and pawns can't push")
        }
    }

    private fun isKingPiece(x: Int, y: Int): Boolean {
        return board.getSquare(x, y).piece is King
    }

    private fun verifyAdjacent(startX: Int, startY: Int, endX: Int, endY: Int) {
        if (!isAdjacent(startX, startY, endX, endY)) {
            throw IllegalEventException(IllegalEventReason.NON_ADJACENT_SQUARE, Coordinate(endX, endY),
                    "can't push: square at ($endX, $endY) is not adjacent")
        }
    }

    private fun isAdjacent(startX: Int, startY: Int, endX: Int, endY: Int): Boolean {
        return (startX == endX && Math.abs(startY - endY) == 1) || (startY == endY && Math.abs(startX - endX) == 1)
    }

    private fun verifyNoPieceWithHatPreventingPush(startX: Int, startY: Int, xDelta: Int, yDelta: Int) {
        board.forEachOccupiedSquareAffectedByPotentialPush(startX, startY, xDelta, yDelta)
        { square, x, y ->
            if (square.piece is King && square.piece.hatted) {
                throw IllegalEventException(IllegalEventReason.HATTED_KING, Coordinate(x, y),
                        "can't push: king at ($x, $y) has a hat")
            }
        }
    }

    private fun verifyNoRailPreventingPush(startX: Int, startY: Int, xDelta: Int, yDelta: Int) {
        if (xDelta == 0) {
            // y axis push - no rails here
            return
        }

        var last: Triple<BoardSquare, Int, Int>? = null
        board.forEachOccupiedSquareAffectedByPotentialPush(startX, startY, xDelta, yDelta) { square, x, y ->
            last = Triple(square, x, y)
        }

        val (square, x, y) = last!!

        if (square.rail != Rail.NONE) {
            throw IllegalEventException(IllegalEventReason.RAIL_PREVENTING_PUSH, Coordinate(x, y),
                    "can't push: square at ($x, $y) has a rail")
        }
    }

    private fun applyInitialPlacement(player: Player, placement: InitialPlacementCommand): GameState {
        val pieceToPlace = setup.getUnplacedPiece(getPlayerNumber(player), placement.pieceType)

        val updatedSetup = setup.withPiecePlaced(getPlayerNumber(player), placement.pieceType)
        val updatedBoard = board.withSquareWithPiece(placement.x, placement.y, pieceToPlace)

        return GameState(config, updatedSetup, turn, updatedBoard)
    }

    private fun applyMove(startX: Int, startY: Int, endX: Int, endY: Int, asMoveTurn: Boolean = false): GameState {
        val movingPiece = board.getSquare(startX, startY).piece
        val player = movingPiece!!.owner

        val updatedBoard = board
                .withSquareWithPiece(startX, startY, null)
                .withSquareWithPiece(endX, endY, movingPiece)

        val updatedTurn = if (asMoveTurn) turn.withMovesIncremented() else turn

        // suicide
        val suicide = asMoveTurn && updatedTurn.moves == 2 && !isPiecePushable(updatedBoard, player)
        val result = if (suicide) ResultState(getOpponent(player)) else null

        return GameState(config, setup, updatedTurn, updatedBoard, result)
    }

    private fun applySetupConfirmed(player: Player): GameState {
        return GameState(config, setup.withPlayerSetupComplete(getPlayerNumber(player)), turn, board)
    }

    private fun applyPush(startX: Int, startY: Int, xDelta: Int, yDelta: Int): GameState {
        var updatedBoard = board

        val hatCoords = board.coordsOfHattedKing()

        if (hatCoords != null) {
            updatedBoard = updatedBoard.withSquareWithPiece(hatCoords.first, hatCoords.second,
                    (board.getSquare(hatCoords.first, hatCoords.second).piece as King).withHat(false))
        }

        var movingPiece: Piece? = null
        var lastPushedCoord: Triple<BoardSquare, Int, Int>? = null

        board.forEachOccupiedSquareAffectedByPotentialPush(startX, startY, xDelta, yDelta) { square, x, y ->
            updatedBoard = updatedBoard.withSquareWithPiece(x, y, movingPiece)

            movingPiece = square.piece

            if (x == startX && y == startY) {
                movingPiece = (movingPiece as King).withHat()
            }

            lastPushedCoord = Triple(square, x, y)
        }

        val (lastPushedSquare, lastPushedX, lastPushedY) = lastPushedCoord!!
        val finalX = lastPushedX + xDelta
        val finalY = lastPushedY + yDelta

        var result: ResultState? = null

        if (board.isSquare(finalX, finalY)) {
            updatedBoard = updatedBoard.withSquareWithPiece(finalX, finalY, movingPiece)
        } else {
            // victory!!
            result = ResultState(getOpponent(lastPushedSquare.piece!!.owner))
        }

        return GameState(config, setup,
                turn.next(getOpponent(turn.player), isPlayer2 = (turn.player == config.player2)), updatedBoard, result)
    }

    private fun applyResign(player: Player): GameState {
        return GameState(config, setup, turn, board, ResultState(getOpponent(player), true))
    }

    private fun isPiecePushable(board: Board, player: Player): Boolean {
        // create a temporary state for the prospective board so we can call the methods affecting its internal
        // board reference
        val working = GameState(config, setup, turn, board)

        return board.getBoardSquareCoords { square -> square.piece is King && square.piece.owner == player }
                .any { (startX, startY) ->
                    board.getNeighbourCoords(startX, startY) { square -> square.piece != null }
                            .any { (endX, endY) ->
                                val xDelta = endX - startX
                                val yDelta = endY - startY

                                val preventedByHat = ifIllegalEvent(IllegalEventReason.HATTED_KING) {
                                    working.verifyNoPieceWithHatPreventingPush(startX, startY, xDelta, yDelta)
                                }

                                val preventedByRail = ifIllegalEvent(IllegalEventReason.RAIL_PREVENTING_PUSH) {
                                    working.verifyNoRailPreventingPush(startX, startY, xDelta, yDelta)
                                }

                                !preventedByHat && !preventedByRail
                            }
                }
    }

    private fun ifIllegalEvent(reason: IllegalEventReason, action: () -> Unit): Boolean {
        try {
            action.invoke()
        } catch (ex: IllegalEventException) {
            if (ex.reason == reason) {
                return true
            }
            throw ex
        }
        return false
    }

    private fun getPlayerNumber(player: Player): Int {
        return if (player == config.player1) 1 else 2
    }

    private fun getOpponent(player: Player): Player {
        return if (player == config.player1) config.player2 else config.player1
    }
}
