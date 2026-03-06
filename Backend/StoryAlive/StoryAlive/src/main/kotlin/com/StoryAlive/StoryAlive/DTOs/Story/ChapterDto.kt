package com.StoryAlive.StoryAlive.DTOs.Story

data class ChapterDto(
    val chapterId: String,
    val title: SentenceDto,
    val scenes: ArrayList<SceneDto>
)
