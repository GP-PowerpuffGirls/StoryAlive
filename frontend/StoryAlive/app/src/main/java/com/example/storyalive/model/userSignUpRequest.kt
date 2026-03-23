package com.example.storyalive.model

data class UserSignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val age: Int,
    val preferencesTags: List<String>
)
