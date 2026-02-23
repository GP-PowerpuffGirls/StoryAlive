package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class StoryController {

    @GetMapping("/story")
    fun getStories( @AuthenticationPrincipal user: CustomUserDetails) {
        println("Accessing /story endpoint")
        val userId = user.getUserId()
        print("User ID from token: $userId")
    }

}