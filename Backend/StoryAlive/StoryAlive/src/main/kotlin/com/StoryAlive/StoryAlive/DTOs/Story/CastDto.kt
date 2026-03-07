package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Gender

data class CastDto(val name: String,
                   val gender: Gender,
                   val isAdult: Boolean,
                   var voiceReference: String)
