package com.StoryAlive.StoryAlive.Security

import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bson.types.ObjectId
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {

            val token = authHeader.substring(7)

            if (jwtService.validateAccessToken(token)) {

                val userId = jwtService.extractUserId(token)

                if (userId.isNotEmpty()) {

                    val userDetails = CurrentUserDetails(ObjectId(userId))

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }

        } catch (e: Exception) {
            // Ignore token errors and continue request
        }

        filterChain.doFilter(request, response)
    }
}