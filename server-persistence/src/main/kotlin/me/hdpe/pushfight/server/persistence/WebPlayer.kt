package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.Player

data class WebPlayer(val number: Int, val accountId: String, val playerName: String) : Player {
}