package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity

data class Audio(
    val emotion: Emotion,
    val intensity: Intensity,
    val filepath: String
)
