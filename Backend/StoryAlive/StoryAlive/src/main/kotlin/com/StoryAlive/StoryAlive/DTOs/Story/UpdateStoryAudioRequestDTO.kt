package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity

data class UpdateStoryAudioRequestDTO(
    val story: StoryCreationDTO,
    val sentenceId: String,
    val emotion: Emotion,
    val intensity: Intensity
)
