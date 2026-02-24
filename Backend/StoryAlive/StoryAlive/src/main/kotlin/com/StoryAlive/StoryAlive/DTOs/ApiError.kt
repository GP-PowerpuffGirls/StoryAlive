package com.StoryAlive.StoryAlive.DTOs

data class ApiError(
    val status: Int,
    val message: String,
    val path: String,
    val timestamp: String,
    val errors: Map<String, String>? = null
)
