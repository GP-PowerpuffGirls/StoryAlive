package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags


public data class StoryRequestDTO(
    val title: String,
    val description: String,
    val voiceActors: MutableMap<String, Pair<String,String>>? = mutableMapOf(),
    val genre: Genre,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val tags: List<Tags> = emptyList(),
    val minimumAge: Int

    )
