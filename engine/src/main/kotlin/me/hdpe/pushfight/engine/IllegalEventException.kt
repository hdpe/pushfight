package me.hdpe.pushfight.engine

class IllegalEventException(val reason: IllegalEventReason, val coordinate: Coordinate? = null, message: String) :
        RuntimeException(message)
