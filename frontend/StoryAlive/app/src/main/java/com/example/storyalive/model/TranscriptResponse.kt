package com.example.storyalive.model

data class TranscriptResponse(
    val chapters: List<Chapter>
)
data class Chapter(
    val chapterId: String,
    val scenes: List<Scene>
)

data class Scene(
    val sceneId: String,
    val sentences: List<Sentence>
)

data class Sentence(
    val speaker: String,
    val sentenceId: String,
    val sentence: String
)

data class TimedSentence(
    val sentenceId: String,
    val text: String,
    val speaker: String,
    val start: Double,
    val end: Double
)