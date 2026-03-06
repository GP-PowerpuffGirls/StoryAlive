package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Emotion

data class SentenceDto(
    private val speaker: String,
    private val sentenceId: String,
    private val sentence: String,
    private var prosodyReference: String,
    private val emotion: Emotion,
    private val intensity: Int
)
