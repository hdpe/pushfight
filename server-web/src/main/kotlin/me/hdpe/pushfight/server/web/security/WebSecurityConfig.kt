package me.hdpe.pushfight.server.web.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class WebSecurityConfig(val authenticationProvider: AccessKeyAuthenticationProvider,
                        val signingKeyProvider: JwtSigningKeyProvider,
                        val securityContextAccessor: SecurityContextAccessor) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
                .anyRequest()
                    .authenticated()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf()
                .disable()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun configure(web: WebSecurity) {
        web
            .ignoring()
                .antMatchers("/token", "/error", "/v2/api-docs", "/", "/docs", "/docs/**", "/favicon.ico")
    }

    private fun jwtAuthenticationFilter() = JwtAuthenticationFilter(signingKeyProvider, securityContextAccessor)
}