package com.StoryAlive.StoryAlive.DTOs.Story

data class StoryCreationDTO(
    private val storyId: String,
    private val chapters: ArrayList<ChapterDto>,
    private val cast: ArrayList<CastDto>
)
