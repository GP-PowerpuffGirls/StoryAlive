package com.StoryAlive.StoryAlive.DTOs.Story

data class TTSResponse(
    val fileName: String,
    val duration: Double,
    val audioBase64: String
)
