package com.StoryAlive.StoryAlive.DTOs.Story

public data class ChapterDto(
    val chapterId: String,
    val title: SentenceDto,
    val scenes: ArrayList<SceneDto>
)
