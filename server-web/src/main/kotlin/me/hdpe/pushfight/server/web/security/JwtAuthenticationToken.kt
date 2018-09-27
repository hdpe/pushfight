package me.hdpe.pushfight.server.web.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtAuthenticationToken(val accountDetails: AccountDetails) :
        AbstractAuthenticationToken(listOf(SimpleGrantedAuthority("ROLE_USER"))) {

    init {
        isAuthenticated = true
    }

    override fun getPrincipal(): Any = accountDetails

    override fun getCredentials(): Any? = null
}