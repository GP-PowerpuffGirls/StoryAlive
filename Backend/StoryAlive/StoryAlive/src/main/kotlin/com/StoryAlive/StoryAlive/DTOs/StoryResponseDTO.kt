package com.StoryAlive.StoryAlive.DTOs

import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import java.time.Instant

data class StoryResponseDTO(
    val storyId: String,
    val creatorId: String,
    val voiceActors: Map<String, Pair<String, String>>,
    val title: String,
    val description: String,
    val tags: List<Tags>,
    val genre: Genre,
    val duration: Double,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val finalAudioPath: String,
    val jsonPath: String,
    val pdfPath: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val minimumAge: Int,
    val numberOfViews: Int
)