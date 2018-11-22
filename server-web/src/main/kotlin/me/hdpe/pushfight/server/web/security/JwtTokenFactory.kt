package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class JwtTokenFactory(val clock: Clock, val signingKeyProvider: JwtSigningKeyProvider) {

    fun create(clientDetails: ClientDetails): String {

        return Jwts.builder()
                .setSubject(clientDetails.id)
                .claim("name", clientDetails.name)
                .claim("fixedAccountId", clientDetails.fixedAccountId)
                .claim("exp", clock.instant().epochSecond + (60 * 60))
                .signWith(signingKeyProvider.key)
                .compact()
    }
}