package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.RefreshRequest
import com.StoryAlive.StoryAlive.DTOs.UserLoginRequest
import com.StoryAlive.StoryAlive.DTOs.UserSignupRequest
import com.StoryAlive.StoryAlive.Services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController (val authService: AuthService) {

    @PostMapping("/login")
    fun login( @RequestBody user: UserLoginRequest ) {
        authService.login(user)
    }

    @PostMapping("/register")
    fun register( @RequestBody user : UserSignupRequest ){
        authService.registerUser(user)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest) {
        authService.refresh(request.refreshToken)
    }
}