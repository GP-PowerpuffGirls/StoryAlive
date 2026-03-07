package com.StoryAlive.StoryAlive.DTOs.Story

import lombok.Data


data class StoryCreationDTO(
     var storyId: String? = null,
     val chapters: ArrayList<ChapterDto>,
     val cast: ArrayList<CastDto>
)
