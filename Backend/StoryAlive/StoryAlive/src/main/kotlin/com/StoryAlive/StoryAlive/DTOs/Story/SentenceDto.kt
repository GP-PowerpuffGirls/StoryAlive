package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity

data class SentenceDto(
    var speaker: String,
    val sentenceId: String,
    val sentence: String,
    var prosodyReference: String,
    val emotion: Emotion,
    val intensity: Intensity
)
