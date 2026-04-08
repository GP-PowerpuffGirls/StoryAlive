package com.StoryAlive.StoryAlive.DTOs

import org.bson.types.ObjectId
import java.time.Instant

data class UserDTO(

    val firstName: String,
    val lastName: String,
    val email: String,
    val accountCreationDate: Instant = Instant.now(),
    val age: Int,
    val favouriteVoiceActors: List<ObjectId>? = emptyList(),
    val totalPublishedStoriesCount: Int = 0,
    val totalVoiceActorsCount: Int = 0,
    val totalStoriesCount: Int = 0,

    )
