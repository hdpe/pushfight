package me.hdpe.pushfight.server.web.game

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadPlayRequestException(message: String) : RuntimeException(message)
