package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.RefreshRequest
import com.StoryAlive.StoryAlive.DTOs.TokenPair
import com.StoryAlive.StoryAlive.DTOs.UserLoginRequest
import com.StoryAlive.StoryAlive.DTOs.UserSignupRequest
import com.StoryAlive.StoryAlive.Services.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController (val authService: AuthService) {

    @PostMapping("/login")
    fun login( @Valid @RequestBody user: UserLoginRequest ) : ResponseEntity<TokenPair> {
        val tokens = authService.login(user)
        return ResponseEntity.ok(tokens)
    }

    @PostMapping("/register")
    fun register( @Valid @RequestBody user : UserSignupRequest ): ResponseEntity<TokenPair> {
        val tokens = authService.registerUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens)
    }

    @PostMapping("/refresh")
    fun refresh( @Valid @RequestBody request: RefreshRequest): ResponseEntity<TokenPair> {
        val tokens = authService.refresh(request.refreshToken)
        return ResponseEntity.ok(tokens)
    }

    @PostMapping("/logout")
    fun logout( @RequestHeader("Authorization") authHeader: String ) : ResponseEntity<String> {
        val rawToken = if(authHeader.startsWith("Bearer")) authHeader.removePrefix("Bearer ") else authHeader
        authService.logout(rawToken)
        return ResponseEntity.ok("Logged Out Successfully")
    }

}