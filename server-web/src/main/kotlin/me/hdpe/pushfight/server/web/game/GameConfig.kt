package me.hdpe.pushfight.server.web.game

import me.hdpe.pushfight.engine.BoardFactory
import me.hdpe.pushfight.engine.GameStateFactory
import me.hdpe.pushfight.engine.SetupStateFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GameConfig {

    @Bean
    fun gameStateFactory(): GameStateFactory = GameStateFactory(BoardFactory(), SetupStateFactory())
}