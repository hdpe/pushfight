package me.hdpe.pushfight.server.web.security

import org.springframework.security.core.AuthenticationException

class JwtTokenMissingException(message: String) : AuthenticationException(message)