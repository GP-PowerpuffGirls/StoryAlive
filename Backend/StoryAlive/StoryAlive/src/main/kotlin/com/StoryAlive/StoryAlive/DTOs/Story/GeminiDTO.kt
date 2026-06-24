package com.StoryAlive.StoryAlive.DTOs.Story

data class GeminiStory(
    val cast: List<GeminiCast>,
    val chapters: List<GeminiChapter>
)

data class GeminiCast(
    val name_ar: String?,
    val name: String?,
    val gender: String?,
    val isAdult: Boolean?,
    val evidence: String?,
    val preferredRole: String?
)

data class GeminiChapter(
    val chapter_title: String?,
    val scenes: List<GeminiScene>
)

data class GeminiScene(
    val location: GeminiLocation?,
    val scene_emotion: String?,
    val segments: List<GeminiSegment>
)

data class GeminiLocation(
    val locationName: String?,
    val sfx: String?,
    val path: String?
)

data class GeminiSegment(
    val speaker: String?,
    val sentence: String?,
    val emotion: String?,
    val intensity: String?
)
