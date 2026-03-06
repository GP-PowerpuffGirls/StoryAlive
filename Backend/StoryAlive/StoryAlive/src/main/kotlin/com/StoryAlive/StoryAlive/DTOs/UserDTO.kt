package com.StoryAlive.StoryAlive.DTOs

import org.bson.types.ObjectId
import java.time.Instant

data class UserDTO(

    val firstName: String,
    val lastName: String,
    val email: String,
    val accountCreationDate: Instant = Instant.now(),

    val favouriteVoiceActors: List<ObjectId>? = emptyList(),

    val totalPublishedStoriesCount: Any = 0,
    val totalVoiceActorsCount: Any = 0,
    val totalStoriesCount: Any = 0,

    )
