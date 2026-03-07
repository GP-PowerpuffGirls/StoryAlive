package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.BGMusicEmotion
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "background_music")
data class BackgroundMusic(
    @Id val musicId: ObjectId = ObjectId(),
    val musicPath: String,
    val emotion: BGMusicEmotion?,
    val forKids: Boolean,

    )
