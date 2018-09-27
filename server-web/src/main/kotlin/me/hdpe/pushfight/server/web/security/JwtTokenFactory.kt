package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class JwtTokenFactory(val clock: Clock, val signingKeyProvider: JwtSigningKeyProvider) {

    fun create(playerId: String, playerName: String): String {

        return Jwts.builder()
                .setSubject(playerId)
                .claim("name", playerName)
                .claim("exp", clock.instant().epochSecond + (60 * 60))
                .signWith(signingKeyProvider.key)
                .compact()
    }
}