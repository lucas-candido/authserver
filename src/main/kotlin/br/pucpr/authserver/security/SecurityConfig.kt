package br.pucpr.authserver.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console


@Configuration
@EnableMethodSecurity
@SecurityScheme(
    name = "AuthServer",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
class  SecurityConfig(private val jwtTokenFilter: JwtTokenFilter) {
    @Bean
    fun filterChain(security: HttpSecurity): DefaultSecurityFilterChain =
        security.cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .exceptionHandling()
            .authenticationEntryPoint { _, res, ex -> res.sendError(SC_UNAUTHORIZED, ex.message) }
            .and()
            .headers { it.frameOptions().disable() }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.GET).permitAll()
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()
                    //.requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(toH2Console()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtTokenFilter, BasicAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsFilter() =
        CorsConfiguration().apply {
            addAllowedHeader("*")
            addAllowedOrigin("*")
            addAllowedMethod("*")
        }.let {
            UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", it)
            }
        }.let { CorsFilter(it) }
}