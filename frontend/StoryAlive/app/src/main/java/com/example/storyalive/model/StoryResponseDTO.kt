package com.example.storyalive.model

data class StoryResponseDTO(
    var storyId: String,
    var creatorId: String,
    var voiceActors: Map<String, VoiceActorPair>,
    var title: String,
    var description: String,
    var tags: List<String>,
    var genre: String,
    var duration: Double,
    var isPrivate: Boolean,
    var hasSfx: Boolean,
    var hasBackgroundMusic: Boolean,
    var finalAudioPath: String,
    var jsonPath: String,
    var pdfPath: String,
    var createdAt: String,
    var modifiedAt: String,
    var minimumAge: Int,
    var numberOfViews: Int
)
data class IdObject(
    val date: String,
    val timestamp: Long
)

data class VoiceActorPair(
    val first: String,
    val second: String
)
data class PagedResponses<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int
)

