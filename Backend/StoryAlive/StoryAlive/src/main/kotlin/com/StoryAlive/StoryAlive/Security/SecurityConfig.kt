package com.StoryAlive.StoryAlive.Security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfig (private val jwtAuthFilter: JwtAuthFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{

        return http
            .csrf {csrf ->  csrf.disable()}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .authorizeHttpRequests { auth ->
                auth
                    //* Anyone can access ths controller and its endpoints.
                    .requestMatchers("/auth/**")
                    .permitAll()

                    .dispatcherTypeMatchers(
                        DispatcherType.ERROR,
                        DispatcherType.FORWARD,
                    )
                    .permitAll()

                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    ).permitAll()

                    //* All other endpoints require authentication (i.e. a valid JWT access token).
                    .anyRequest()
                    .authenticated()
            }


            // force Spring Security to return 401 Unauthorized instead of redirecting to a login page when authentication fails.
            .exceptionHandling { configurer ->
                configurer
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

            .build()

    }

}