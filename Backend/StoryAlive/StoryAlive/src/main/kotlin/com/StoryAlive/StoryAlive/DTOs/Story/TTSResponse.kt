package com.StoryAlive.StoryAlive.DTOs.Story

public data class TTSResponse(
    val fileName: String,
    val duration: Double,
    val audioPath: String,
    val storyCreationDTO: StoryCreationDTO
)
