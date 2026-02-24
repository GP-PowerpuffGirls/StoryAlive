package com.StoryAlive.StoryAlive.DTOs

import jakarta.validation.constraints.NotBlank

data class RefreshRequest(
    @field:NotBlank(message = "You must provide a token first")
    val refreshToken: String
)
