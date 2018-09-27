package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils
import javax.crypto.SecretKey

@Service
class JwtSigningKeyProvider(properties: SecurityProperties) {

    val key: SecretKey = Keys.hmacShaKeyFor(Base64Utils.decodeFromString(properties.signingKey))
}