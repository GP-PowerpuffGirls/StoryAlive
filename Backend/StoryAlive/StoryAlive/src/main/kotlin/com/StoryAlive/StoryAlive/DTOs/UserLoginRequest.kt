package com.StoryAlive.StoryAlive.DTOs

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class UserLoginRequest(

//    @field:Email(message = "Invalid email format")
    val email: String,

//    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain both letters and numbers")
    val password: String
)
