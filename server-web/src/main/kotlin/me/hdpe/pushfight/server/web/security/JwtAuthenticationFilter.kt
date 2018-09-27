package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.Jwts
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(val signingKeyProvider: JwtSigningKeyProvider,
                              val securityContextAccessor: SecurityContextAccessor) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val authorizationHeader: String? = request.getHeader("Authorization")
        val bearerPrefix = "Bearer "

        if (authorizationHeader == null || !authorizationHeader.startsWith(bearerPrefix)) {
            throw JwtTokenMissingException("no Authorization header with Bearer")

        } else {
            val jwt = Jwts.parser()
                    .setSigningKey(signingKeyProvider.key)
                    .parseClaimsJws(authorizationHeader.substring(bearerPrefix.length))

            val claims = jwt.body

            val player = AccountDetails(claims.subject, claims["name"] as String, "", null)

            securityContextAccessor.context.authentication = JwtAuthenticationToken(player)
        }

        filterChain.doFilter(request, response)
    }
}