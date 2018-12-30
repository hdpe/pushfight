package me.hdpe.pushfight.server.persistence.game

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.module.SimpleModule
import me.hdpe.pushfight.engine.Piece
import me.hdpe.pushfight.engine.Player
import me.hdpe.pushfight.engine.Square

class PersistenceJacksonModule : SimpleModule() {

    init {
        setMixInAnnotation(Player::class.java, PlayerMixin::class.java)
        setMixInAnnotation(Piece::class.java, PieceMixin::class.java)
        setMixInAnnotation(Square::class.java, SquareMixin::class.java)
    }

    @JsonDeserialize(`as` = WebPlayer::class)
    abstract class PlayerMixin {
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    abstract class PieceMixin {
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    abstract class SquareMixin {
    }
}