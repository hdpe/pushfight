package me.hdpe.pushfight.server.web.game

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class PlayForbiddenException(message: String) : RuntimeException(message)
