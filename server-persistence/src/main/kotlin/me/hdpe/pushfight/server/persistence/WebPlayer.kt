package me.hdpe.pushfight.server.persistence

import me.hdpe.pushfight.engine.Player

data class WebPlayer(val id: String, val accountId: String) : Player {
}