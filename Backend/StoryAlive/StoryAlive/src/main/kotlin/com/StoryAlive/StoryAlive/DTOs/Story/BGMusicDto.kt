package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.BGMusicEmotion

data class BGMusicDto(
    var musicPath: String? = "",
    val emotion: BGMusicEmotion,
    val volume: Float
)
