package com.StoryAlive.StoryAlive.DTOs.Story

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
public data class TTSResponse(
    val fileName: String,
    val duration: Double,
    val audioPath: String,
    val storyCreationDTO: StoryCreationDTO
)
