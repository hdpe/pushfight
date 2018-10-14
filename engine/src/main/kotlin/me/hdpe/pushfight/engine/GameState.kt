package me.hdpe.pushfight.engine

import me.hdpe.pushfight.engine.command.InitialPlacementCommand
import me.hdpe.pushfight.engine.command.PieceType
import me.hdpe.pushfight.engine.command.UpdatedPlacementCommand

class GameState(val config: GameConfig, val setup: SetupState, val turn: TurnState, val board: Board,
                val victor: Player? = null) {

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

        return applyMove(startX, startY, endX, endY, incrementMoves = true)
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

    private fun verifyGameInProgress() {
        if (victor != null) {
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
        if (isOpponentPieceInHalf(player, y)) {
            throw IllegalEventException(IllegalEventReason.OPPONENT_HALF, Coordinate(x, y),
                    "square at ($x, $y) is in opponent's half")
        }
    }

    private fun isOpponentPieceInHalf(player: Player, y: Int): Boolean {
        return board.getSquaresInHalf(y).any { it.piece?.owner == getOpponent(player) }
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

    private fun getVacantNeighbours(x: Int, y: Int): Array<Pair<Int, Int>> {
        val candidateNeighbours = arrayOf(Pair(x - 1, y), Pair(x + 1, y), Pair(x, y - 1), Pair(y, y + 1))

        return candidateNeighbours.filter { (nx, ny) -> board.isSquare(nx, ny) && board.isVacant(nx, ny) }.toTypedArray()
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
        val updatedBoard = board.withSquare(placement.x, placement.y, BoardSquare(pieceToPlace))

        return GameState(config, updatedSetup, turn, updatedBoard)
    }

    private fun applyMove(startX: Int, startY: Int, endX: Int, endY: Int, incrementMoves: Boolean = false): GameState {
        val movingPiece = board.getSquare(startX, startY).piece

        val updatedBoard = board
                .withSquare(startX, startY, BoardSquare(null))
                .withSquare(endX, endY, BoardSquare(movingPiece))

        val updatedTurn = if (incrementMoves) turn.withMovesIncremented() else turn

        return GameState(config, setup, updatedTurn, updatedBoard)
    }

    private fun applySetupConfirmed(player: Player): GameState {
        return GameState(config, setup.withPlayerSetupComplete(getPlayerNumber(player)), turn, board)
    }

    private fun applyPush(startX: Int, startY: Int, xDelta: Int, yDelta: Int): GameState {
        var updatedBoard = board

        val hatCoords = board.coordsOfHattedKing()

        if (hatCoords != null) {
            updatedBoard = updatedBoard.withSquare(hatCoords.first, hatCoords.second,
                    BoardSquare((board.getSquare(hatCoords.first, hatCoords.second).piece as King).withHat(false)))
        }

        var movingPiece: Piece? = null
        var lastPushedCoord: Triple<BoardSquare, Int, Int>? = null

        board.forEachOccupiedSquareAffectedByPotentialPush(startX, startY, xDelta, yDelta) { square, x, y ->
            updatedBoard = updatedBoard.withSquare(x, y, BoardSquare(movingPiece))

            movingPiece = square.piece

            if (x == startX && y == startY) {
                movingPiece = (movingPiece as King).withHat()
            }

            lastPushedCoord = Triple(square, x, y)
        }

        val (lastPushedSquare, lastPushedX, lastPushedY) = lastPushedCoord!!
        val finalX = lastPushedX + xDelta
        val finalY = lastPushedY + yDelta

        var victor: Player? = null

        if (board.isSquare(finalX, finalY)) {
            updatedBoard = updatedBoard.withSquare(finalX, finalY, BoardSquare(movingPiece))
        } else {
            // victory!!
            victor = getOpponent(lastPushedSquare.piece!!.owner)
        }

        return GameState(config, setup,
                turn.next(getOpponent(turn.player), isPlayer2 = (turn.player == config.player2)), updatedBoard, victor)
    }

    private fun getPlayerNumber(player: Player): Int {
        return if (player == config.player1) 1 else 2
    }

    private fun getOpponent(player: Player): Player {
        return if (player == config.player1) config.player2 else config.player1
    }
}
