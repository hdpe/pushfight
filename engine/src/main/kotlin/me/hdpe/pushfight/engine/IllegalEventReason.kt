package me.hdpe.pushfight.engine

enum class IllegalEventReason {

    GAME_ENDED,

    PLAYER_SETUP_COMPLETE,

    UNAVAILABLE_PIECE,

    PIECES_NOT_PLACED,

    INCOMPLETE_SETUP,

    OUT_OF_TURN,

    NO_MOVES_REMAINING,

    NO_SUCH_SQUARE,

    SQUARE_EMPTY,

    WRONG_PLAYER_PIECE,

    SQUARE_OCCUPIED,

    NO_PATH_TO_SQUARE,

    PAWN_CANNOT_PUSH,

    NON_ADJACENT_SQUARE,

    HATTED_KING,

    RAIL_PREVENTING_PUSH
}