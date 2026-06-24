package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity

public data class SentenceDto(
    var speaker: String,
    val sentenceId: String,
    val sentence: String,
    var prosodyReference: String,
    val emotion: Emotion,
    val intensity: Intensity,
    var duration: Float=0.0f,
    var audioPath: String=""
)
