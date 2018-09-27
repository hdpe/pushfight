package me.hdpe.pushfight.server.web.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AccessKeyAuthenticationProvider(val accountDetailsProvider: AccountDetailsProvider) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        if (authentication !is AccessKeyAuthenticationToken) {
            return null
        }

        val principal: AccountDetails? = accountDetailsProvider.accounts.find {
            it.accessKeyId == authentication.accessKeyId && it.secret == authentication.secret }
                ?: throw BadCredentialsException("Bad credentials")

        return AccessKeyAuthenticationToken(authentication.accessKeyId, principal = principal)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return AccessKeyAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}