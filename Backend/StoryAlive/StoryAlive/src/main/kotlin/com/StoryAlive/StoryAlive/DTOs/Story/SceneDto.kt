package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.LocationName

data class SceneDto(
    private val sceneId: String,
    private val location: LocationDto,
    val sentences: ArrayList<SentenceDto>,
    private val bgMusic: BGMusicDto,

)
