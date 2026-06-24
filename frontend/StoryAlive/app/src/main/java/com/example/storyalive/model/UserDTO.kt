package com.example.storyalive.model


data class UserResponse(

    val firstName: String,
    val lastName: String,
    val email: String,
    val accountCreationDate: String,
    val age: Int,
    val favouriteVoiceActors: List<String>,
    val totalPublishedStoriesCount: Int,
    val totalVoiceActorsCount: Int,
    val totalStoriesCount: Int,

    )

