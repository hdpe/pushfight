package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.Jwts
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.WebAttributes
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(val signingKeyProvider: JwtSigningKeyProvider,
                              val securityContextAccessor: SecurityContextAccessor) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            securityContextAccessor.context.authentication = attemptAuthentication(request)

            filterChain.doFilter(request, response)
        }
        catch (exception: AuthenticationException) {
            logger.info("authentication failed", exception)

            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception)

            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
        }
    }

    private fun attemptAuthentication(request: HttpServletRequest): JwtAuthenticationToken {
        val authorizationHeader: String? = request.getHeader("Authorization")
        val bearerPrefix = "Bearer "

        if (authorizationHeader == null || !authorizationHeader.startsWith(bearerPrefix)) {
            throw InvalidJwtTokenException("no Authorization header with Bearer")
        }

        try {
            val jwt = Jwts.parser()
                    .setSigningKey(signingKeyProvider.key)
                    .parseClaimsJws(authorizationHeader.substring(bearerPrefix.length))

            val claims = jwt.body

            val player = ClientDetails(claims.subject, claims["name"] as String, "", null,
                    claims["fixedAccountId"] as String?)

            return JwtAuthenticationToken(player)
        }
        catch (ex: Exception) {
            throw InvalidJwtTokenException("error parsing JWT token", ex)
        }
    }
}