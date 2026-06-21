package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.BGMusicEmotion

public data class BGMusicDto(
    var musicPath: String? = "",
    val emotion: BGMusicEmotion,
    val volume: Float
)
