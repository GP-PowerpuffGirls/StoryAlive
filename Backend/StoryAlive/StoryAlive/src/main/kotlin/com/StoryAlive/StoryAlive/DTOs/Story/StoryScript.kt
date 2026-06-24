package com.StoryAlive.StoryAlive.DTOs.Story

data class StoryScript(
    val cast: List<Cast>,
    val chapters: List<Chapter>
)
data class Cast(
    val name: String,
    val gender: String,
    val isAdult: Boolean,
    val preferredRole: String,
    val voiceReference: String
)
data class Chapter(
    val chapterId: Int,
    val title: Sentence,
    val scenes: List<Scene>
)
data class Scene(
    val sceneId: Int,
    val location: Location,
    val bgMusic: BgMusic,
    val sentences: List<Sentence>
)
data class Sentence(
    val sentenceId: String,
    val speaker: String,
    val sentence: String,
    val emotion: String,
    val intensity: String,
    val prosodyReference: String
)
data class Location(
    val locationName: String,
    val sfx: String,
    val path: String
)
data class BgMusic(
    val volume: Double,
    val emotion: String
)
