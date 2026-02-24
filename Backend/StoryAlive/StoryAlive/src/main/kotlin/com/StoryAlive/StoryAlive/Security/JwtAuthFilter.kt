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
class JwtAuthFilter (private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")  //Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

        if(authHeader!=null && authHeader.startsWith("Bearer ")){ // Check if the header is present
            if(jwtService.validateAccessToken(authHeader)) {  // validate the token

                val userId = jwtService.extractUserId(authHeader) // extract the user id from the token

                // ! Handle Null credentials
//                val auth = UsernamePasswordAuthenticationToken(userId, null) //    You’re basically telling Spring: “Trust me. This user is authenticated.”
                val userDetails = CurrentUserDetails(ObjectId( userId))
                val auth = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )

                // SecurityContextHolder stores current user
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}