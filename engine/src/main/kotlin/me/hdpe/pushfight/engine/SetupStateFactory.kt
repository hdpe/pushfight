package me.hdpe.pushfight.engine

class SetupStateFactory {
    fun create(config: GameConfig): SetupState {
        return SetupState(
                PlayerSetupState(createPieces(config.player1)),
                PlayerSetupState(createPieces(config.player2))
        )
    }

    private fun createPieces(player: Player): List<Piece> {
        return listOf(Pawn(player), Pawn(player), King(player), King(player), King(player))
    }
}