package com.StoryAlive.StoryAlive.Models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class User(
    @Id val userId: ObjectId = ObjectId(),
    val firstName: String,
    val lastName: String,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val age: Int,
    val favouriteStories: List<ObjectId> = emptyList(),
    val favouriteVoiceActors: List<ObjectId> = emptyList(),
    val historyStories: List<ObjectId> = emptyList(),
    val accountCreationDate: Instant = Instant.now(),
    val totalPublishedStoriesCount: Int = 0,
    val totalVoiceActorsCount: Int = 0,
    val totalStoriesCount: Int = 0,
)
