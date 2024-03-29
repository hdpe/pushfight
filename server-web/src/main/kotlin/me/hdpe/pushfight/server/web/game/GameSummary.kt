package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.ResultState
import me.hdpe.pushfight.engine.TurnState
import java.time.ZonedDateTime

class GameSummary(val id: String, val opponentId: String, val opponentName: String, val currentTurn: TurnState?,
                  val playerSetupComplete: Boolean, val playerIndex1: Int, val result: ResultState?,
                  val lastModified: ZonedDateTime?) {
}