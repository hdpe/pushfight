package me.hdpe.pushfight.server.web.security

import org.springframework.security.core.AuthenticationException
import java.lang.Exception

class InvalidJwtTokenException : AuthenticationException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Exception) : super(message, cause)
}