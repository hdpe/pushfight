package me.hdpe.pushfight.server.persistence.game

import me.hdpe.pushfight.engine.Player

data class WebPlayer(override val number: Int, val accountId: String, val playerName: String) : Player {
}