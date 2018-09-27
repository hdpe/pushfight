package me.hdpe.pushfight.engine

class TurnState(val number: Int = 1, val player: Player, val moves: Int = 0) {

    fun withNumberIncremented(): TurnState {
        return TurnState(number + 1, player, moves)
    }

    fun withPlayer(player: Player): TurnState {
        return TurnState(number, player, moves)
    }

    fun withMovesIncremented(): TurnState {
        return TurnState(number, player, moves + 1)
    }
}