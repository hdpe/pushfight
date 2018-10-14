package me.hdpe.pushfight.engine

class King(override val owner: Player, val hatted: Boolean = false) : Piece {

    fun withHat(bool: Boolean = true): King {
        return King(owner, bool)
    }
}
