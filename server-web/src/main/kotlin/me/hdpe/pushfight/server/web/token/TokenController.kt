package me.hdpe.pushfight.server.web.token

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import me.hdpe.pushfight.server.web.AuthenticationEndpointRequestApiResponses
import me.hdpe.pushfight.server.web.security.AccessKeyAuthenticationToken
import me.hdpe.pushfight.server.web.security.JwtTokenFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Api(tags = ["Authentication"])
class TokenController(val tokenFactory: JwtTokenFactory, val authenticationManager: AuthenticationManager) {

    @PostMapping("/token")
    @ApiOperation(value = "Get Token", nickname = "token")
    @AuthenticationEndpointRequestApiResponses
    fun token(@Valid @RequestBody request: TokenRequest): TokenResponse {
        val authentication = authenticationManager.authenticate(AccessKeyAuthenticationToken(request.accessKeyId!!,
                request.secret!!)) as AccessKeyAuthenticationToken

        val accountDetails = authentication.principal!!

        return TokenResponse(tokenFactory.create(accountDetails.id, accountDetails.name))
    }
}
