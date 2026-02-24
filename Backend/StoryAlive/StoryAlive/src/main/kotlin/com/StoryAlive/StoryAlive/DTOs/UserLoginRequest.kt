package com.StoryAlive.StoryAlive.DTOs

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserLoginRequest(

    @field:Email(message = "Invalid Email Format")
    val email: String,

    @field:NotBlank(message = "Please provide a password")
    val password: String
)
