package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.ResultState
import me.hdpe.pushfight.engine.TurnState

class GameSummary(val id: String, val opponentId: String, val opponentName: String, val currentTurn: TurnState,
                  val result: ResultState?) {
}