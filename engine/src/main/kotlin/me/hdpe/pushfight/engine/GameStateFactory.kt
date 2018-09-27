package me.hdpe.pushfight.engine

class GameStateFactory(val boardFactory: BoardFactory, val setupStateFactory: SetupStateFactory) {

    fun create(config: GameConfig): GameState {
        return GameState(config, this.setupStateFactory.create(config), TurnState(player = config.player1),
                this.boardFactory.create())
    }
}