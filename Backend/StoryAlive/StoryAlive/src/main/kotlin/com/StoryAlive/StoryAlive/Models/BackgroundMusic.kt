package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "background_music")
data class BackgroundMusic(
    @Id val musicId: ObjectId = ObjectId(),
    val musicPath: String,
    val emotion: Emotion,
    val intensity: Intensity
)
