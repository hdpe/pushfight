package me.hdpe.pushfight.server.web.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class AccessKeyAuthenticationToken(val accessKeyId: String, val secret: String? = null,
                                   val principal: ClientDetails? = null) : AbstractAuthenticationToken(listOf()) {

    init {
        if (principal != null) {
            isAuthenticated = true
        }
    }

    override fun getCredentials() = secret

    override fun getPrincipal(): Any? = principal
}