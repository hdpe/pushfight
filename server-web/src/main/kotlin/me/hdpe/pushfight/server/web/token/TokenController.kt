package me.hdpe.pushfight.server.web.token

import me.hdpe.pushfight.server.web.security.AccessKeyAuthenticationToken
import me.hdpe.pushfight.server.web.security.JwtTokenFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class TokenController(val tokenFactory: JwtTokenFactory, val authenticationManager: AuthenticationManager) {

    @PostMapping("/token")
    fun token(@Valid @RequestBody request: TokenRequest): TokenResponse {
        val authentication = authenticationManager.authenticate(AccessKeyAuthenticationToken(request.accessKeyId!!,
                request.secret!!)) as AccessKeyAuthenticationToken

        val accountDetails = authentication.principal!!

        return TokenResponse(tokenFactory.create(accountDetails.id, accountDetails.name))
    }
}
