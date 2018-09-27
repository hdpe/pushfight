package me.hdpe.pushfight.engine;

interface Play {
	fun apply(state: GameState): GameState
}
