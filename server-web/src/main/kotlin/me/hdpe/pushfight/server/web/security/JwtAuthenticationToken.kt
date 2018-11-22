package me.hdpe.pushfight.server.web.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtAuthenticationToken(val clientDetails: ClientDetails) :
        AbstractAuthenticationToken(listOf(SimpleGrantedAuthority("ROLE_USER"))) {

    init {
        isAuthenticated = true
    }

    override fun getPrincipal(): Any = clientDetails

    override fun getCredentials(): Any? = null
}