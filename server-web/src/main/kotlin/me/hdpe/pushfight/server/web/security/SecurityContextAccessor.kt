package me.hdpe.pushfight.server.web.security

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityContextAccessor {

    val context: SecurityContext
        get() = SecurityContextHolder.getContext()
}