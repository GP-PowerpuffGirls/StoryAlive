package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.DTOs.BGMusicDto
import com.StoryAlive.StoryAlive.Enums.LocationName

public data class SceneDto(
    val sceneId: String,
    val location: LocationDto,
    val sentences: ArrayList<SentenceDto>,
    val bgMusic: BGMusicDto,

    )
