package me.hdpe.pushfight.server.web.security

import io.jsonwebtoken.JwtException
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
        }
        catch (exception: AuthenticationException) {
            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception)

            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun attemptAuthentication(request: HttpServletRequest): JwtAuthenticationToken {
        val authorizationHeader: String? = request.getHeader("Authorization")
        val bearerPrefix = "Bearer "

        if (authorizationHeader == null || !authorizationHeader.startsWith(bearerPrefix)) {
            throw JwtTokenMissingException("no Authorization header with Bearer")
        }

        val jwt = Jwts.parser()
                .setSigningKey(signingKeyProvider.key)
                .parseClaimsJws(authorizationHeader.substring(bearerPrefix.length))

        val claims = jwt.body

        val player = AccountDetails(claims.subject, claims["name"] as String, "", null)

        return JwtAuthenticationToken(player)
    }
}