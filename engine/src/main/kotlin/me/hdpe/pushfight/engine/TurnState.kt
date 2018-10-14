package me.hdpe.pushfight.engine

class TurnState(val number: Int = 1, val player: Player, val moves: Int = 0) {

    fun next(player: Player, isPlayer2: Boolean): TurnState {
        return TurnState(if (isPlayer2) number + 1 else number, player, 0)
    }

    fun withMovesIncremented(): TurnState {
        return TurnState(number, player, moves + 1)
    }
}