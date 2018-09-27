package me.hdpe.pushfight.engine

import me.hdpe.pushfight.engine.command.PieceType

data class SetupState(val player1Setup: PlayerSetupState, val player2Setup: PlayerSetupState) {

    fun isComplete(): Boolean = player1Setup.complete && player2Setup.complete

    fun isComplete(playerNumber: Int): Boolean = getPlayerSetupState(playerNumber).complete

    fun getUnplacedPiece(playerNumber: Int, pieceType: PieceType): Piece? {
        return getPlayerSetupState(playerNumber).getUnplacedPiece(pieceType)
    }

    fun isUnplacedPiecesRemaining(playerNumber: Int): Boolean {
        return !getPlayerSetupState(playerNumber).unplaced.isEmpty()
    }

    fun withPiecePlaced(playerNumber: Int, pieceType: PieceType): SetupState {
        return withUpdatedState(playerNumber, getPlayerSetupState(playerNumber).withPiecePlaced(pieceType))
    }

    fun withPlayerSetupComplete(playerNumber: Int): SetupState {
        return withUpdatedState(playerNumber, getPlayerSetupState(playerNumber).withComplete())
    }

    private fun withUpdatedState(playerNumber: Int, updatedPlayerState: PlayerSetupState): SetupState {
        return if (playerNumber == 1)
            SetupState(updatedPlayerState, player2Setup)
        else
            SetupState(player1Setup, updatedPlayerState)
    }

    private fun getPlayerSetupState(playerNumber: Int): PlayerSetupState {
        return if (playerNumber == 1) player1Setup else player2Setup
    }
}