package me.hdpe.pushfight.server.persistence.game

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import me.hdpe.pushfight.engine.GameState
import org.springframework.stereotype.Service

@Service
class StateMapper() {

    private final val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.registerModules(KotlinModule(), PersistenceJacksonModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun serialize(state: GameState): String {
        return mapper.writeValueAsString(state)
    }

    fun deserialize(string: String): GameState {
        return mapper.readValue(string, GameState::class.java)
    }
}