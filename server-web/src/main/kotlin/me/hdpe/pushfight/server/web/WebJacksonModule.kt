package me.hdpe.pushfight.server.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.util.StdConverter
import me.hdpe.pushfight.engine.*
import me.hdpe.pushfight.server.persistence.game.WebPlayer

class WebJacksonModule : SimpleModule() {

    init {
        setMixInAnnotation(Piece::class.java, PieceMixin::class.java)
        setMixInAnnotation(Square::class.java, SquareMixin::class.java)
        setMixInAnnotation(BoardSquare::class.java, BoardSquareMixin::class.java)
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
            JsonSubTypes.Type(Pawn::class, name = "pawn"),
            JsonSubTypes.Type(King::class, name = "king")
    )
    abstract class PieceMixin {

        @JsonSerialize(converter = PlayerToNumberConverter::class)
        abstract fun getOwner(): Player
    }

    class PlayerToNumberConverter : StdConverter<Player, Int>() {
        override fun convert(value: Player): Int = (value as WebPlayer).number
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
            JsonSubTypes.Type(AbyssSquare::class, name = "abyss"),
            JsonSubTypes.Type(BoardSquare::class, name = "board")
    )
    abstract class SquareMixin {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    abstract class BoardSquareMixin {

        @JsonSerialize(converter = RailToStringConverter::class)
        abstract fun getRail(): Rail
    }

    class RailToStringConverter : StdConverter<Rail, String>() {
        override fun convert(value: Rail): String? = when(value) {
            Rail.NONE -> null
            Rail.LEFT -> "left"
            Rail.RIGHT -> "right"
        }
    }
}