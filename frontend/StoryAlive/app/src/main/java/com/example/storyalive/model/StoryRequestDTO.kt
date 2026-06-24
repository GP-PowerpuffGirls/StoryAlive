package com.example.storyalive.model


data class VoiceActorDTO(
    val first: String,
    val second: String
)

data class StoryRequestDTO(
    val title: String,
    val description: String,
    val voiceActors: Map<String, VoiceActorDTO>,
    val genre: String,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val tags: List<String>,
    val minimumAge: Int
)
