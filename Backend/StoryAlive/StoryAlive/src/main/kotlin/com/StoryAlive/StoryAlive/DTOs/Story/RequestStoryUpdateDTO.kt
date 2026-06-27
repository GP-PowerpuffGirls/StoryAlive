package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity

data class RequestStoryUpdateDTO(
    var emotion: Emotion,
    val intensity: Intensity
)
